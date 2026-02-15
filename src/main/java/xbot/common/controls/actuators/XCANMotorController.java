package xbot.common.controls.actuators;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.AngularAccelerationUnit;
import edu.wpi.first.units.AngularVelocityUnit;
import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.Measure;
import edu.wpi.first.units.PerUnit;
import edu.wpi.first.units.Unit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.Alert;
import org.apache.logging.log4j.LogManager;
import org.littletonrobotics.junction.Logger;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.controls.io_inputs.XCANMotorControllerInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.logging.AlertGroups;
import xbot.common.logic.LogicUtils;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.Second;
import static edu.wpi.first.units.Units.Volts;

public abstract class XCANMotorController implements DataFrameRefreshable {

    /**
     * The PID mode to use when setting a target position or velocity.
     */
    public enum MotorPidMode {
        /**
         * DutyCycle mode is used to control the motor controller's output as a percentage of the maximum output.
         */
        DutyCycle,

        /**
         * Voltage mode is used to control the motor controller's output as a voltage.
         * It is less impacted by robot battery voltage changes than DutyCycle mode.
         */
        Voltage,

        /**
         * TrapezoidalVoltage mode is used to control the motor controller's output as a voltage,
         * with a trapezoidal profile for acceleration and deceleration.
         */
        TrapezoidalVoltage
    }

    public interface XCANMotorControllerFactory {
        XCANMotorController create(
                CANMotorControllerInfo info,
                String owningSystemPrefix,
                String pidPropertyPrefix,
                XCANMotorControllerPIDProperties defaultPIDProperties
        );

        default XCANMotorController create(
                CANMotorControllerInfo info,
                String owningSystemPrefix,
                String pidPropertyPrefix
        ) {
            return create(info, owningSystemPrefix, pidPropertyPrefix, null);
        }
    }

    /**
     * The maximum voltage that the motor controller can accept.
     * This is typically 12V, but at the start of a match the voltage could be a little higher..
     */
    public static final Voltage MAX_VOLTAGE = Volts.of(13.8);

    public final CANBusId busId;
    public final int deviceId;
    public final PropertyFactory propertyFactory;
    private final String defaultPropertyPrefix;
    private final String pidPropertyPrefix;

    protected Map<Integer, XCANMotorControllerPIDProperties> pidProperties = new HashMap<>();

    protected XCANMotorControllerInputsAutoLogged inputs;
    protected String akitName;

    protected boolean usesPropertySystem = true;
    protected boolean firstPeriodicCall = true;

    protected Map<Integer, DoubleProperty> kPProps = new HashMap<>();
    protected Map<Integer, DoubleProperty> kIProps = new HashMap<>();
    protected Map<Integer, DoubleProperty> kDProps = new HashMap<>();
    protected Map<Integer, DoubleProperty> kStaticFFProps = new HashMap<>();
    protected Map<Integer, DoubleProperty> kVelocityFFProps = new HashMap<>();
    protected Map<Integer, DoubleProperty> kGravityFFProps = new HashMap<>();
    protected DoubleProperty kMaxOutputProps;
    protected DoubleProperty kMinOutputProps;

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(XCANMotorController.class);

    protected BooleanSupplier softwareReverseLimit = () -> false;
    protected BooleanSupplier softwareForwardLimit = () -> false;

    // Scale factors, with pre-computed inverses for performance.
    private Measure<? extends PerUnit<DistanceUnit, AngleUnit>> distancePerMotorRotationsScaleFactor;
    private Measure<? extends PerUnit<AngleUnit, DistanceUnit>> distancePerMotorRotationsInverseScaleFactor;
    private Measure<? extends PerUnit<AngleUnit, AngleUnit>> angleScaleFactor;
    private Measure<? extends PerUnit<AngleUnit, AngleUnit>> angleInverseScaleFactor;
    private Measure<? extends PerUnit<AngularVelocityUnit, AngularVelocityUnit>> angularVelocityScaleFactor;
    private Measure<? extends PerUnit<AngularVelocityUnit, AngularVelocityUnit>> angularVelocityInverseScaleFactor;

    private final Alert unhealthyAlert;

    private static final int totalPidSlot = 4;
    private int currentPidSlot = 0;

    protected XCANMotorController(
            CANMotorControllerInfo info,
            String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            String pidPropertyPrefix,
            XCANMotorControllerPIDProperties defaultPIDProperties,
            DataFrameRegistry dataFrameRegistry
    ) {
        this.busId = info.busId();
        this.deviceId = info.deviceId();
        this.propertyFactory = propertyFactory;

        this.inputs = new XCANMotorControllerInputsAutoLogged();

        this.defaultPropertyPrefix = owningSystemPrefix + "/" + info.name();
        this.pidPropertyPrefix = owningSystemPrefix + "/" + pidPropertyPrefix;

        this.propertyFactory.setPrefix(defaultPropertyPrefix);

        police.registerDevice(DevicePolice.DeviceType.CAN, busId, info.deviceId(), info.name());
        this.akitName = info.name()+"/CANMotorController";

        this.unhealthyAlert = new Alert(AlertGroups.DEVICE_HEALTH, "Motor Controller " + info.deviceId() + " on CAN bus " + busId.toString() +  " ("
                + owningSystemPrefix + ") is unhealthy",
                Alert.AlertType.kError);

        if (defaultPIDProperties == null) {
            // If the controller wasn't given a default PID configuration, we shouldn't create
            // matching P/I/D/F/... properties. Properties do have a small performance cost to the robot,
            // and we typically only need them for a subset of motor controllers on the robot - simpler
            // "open loop" controllers don't need them.
            usesPropertySystem = false;
        } else  {
            // Min/max output are not settable via slots on our primary motor controller type
            propertyFactory.setPrefix(this.pidPropertyPrefix);
            kMaxOutputProps = propertyFactory.createPersistentProperty("kMaxOutput", defaultPIDProperties.maxPowerOutput());
            kMinOutputProps = propertyFactory.createPersistentProperty("kMinOutput", defaultPIDProperties.minPowerOutput());

            for (int slot = 0; slot < totalPidSlot; slot++) {
                propertyFactory.setPrefix(this.pidPropertyPrefix + "/" + slot);

                kPProps.put(slot, propertyFactory.createPersistentProperty("kP", defaultPIDProperties.p()));
                kIProps.put(slot, propertyFactory.createPersistentProperty("kI", defaultPIDProperties.i()));
                kDProps.put(slot, propertyFactory.createPersistentProperty("kD", defaultPIDProperties.d()));
                kStaticFFProps.put(slot, propertyFactory.createPersistentProperty("kStaticFeedForward", defaultPIDProperties.staticFeedForward()));
                kVelocityFFProps.put(slot, propertyFactory.createPersistentProperty("kVelocityFeedForward", defaultPIDProperties.velocityFeedForward()));
                kGravityFFProps.put(slot, propertyFactory.createPersistentProperty("kGravityFeedForward", defaultPIDProperties.gravityFeedForward()));

                pidProperties.put(slot, new XCANMotorControllerPIDProperties(
                        kPProps.get(slot).get(),
                        kIProps.get(slot).get(),
                        kDProps.get(slot).get(),
                        kStaticFFProps.get(slot).get(),
                        kVelocityFFProps.get(slot).get(),
                        kGravityFFProps.get(slot).get(),
                        kMaxOutputProps.get(),
                        kMinOutputProps.get())
                );

            }
            this.propertyFactory.setPrefix(this.defaultPropertyPrefix);
        }

        dataFrameRegistry.register(this);
    }

    public abstract void setConfiguration(CANMotorControllerOutputConfig outputConfig);

    /**
     * Set the software forward limit for the motor controller.
     * If the limit is hit, the motor controller will stop.
     * This is evaluated on every periodic call.
     * @param softwareForwardLimit A supplier that returns true if the forward limit is hit.
     */
    public void setSoftwareForwardLimit(BooleanSupplier softwareForwardLimit) {
        this.softwareForwardLimit = softwareForwardLimit;
    }

    /**
     * Set the software reverse limit for the motor controller.
     * If the limit is hit, the motor controller will stop.
     * This is evaluated on every periodic call.
     * @param softwareReverseLimit A supplier that returns true if the reverse limit is hit.
     */
    public void setSoftwareReverseLimit(BooleanSupplier softwareReverseLimit) {
        this.softwareReverseLimit = softwareReverseLimit;
    }

    /**
     * Set the PID values for the motor controller directly, without using the property system.
     * This method sets the PID values on slot 0, and leaves feed forward values at 0.
     * @param p The proportional gain to set.
     * @param i The integral gain to set.
     * @param d The derivative gain to set.
     */
    public void setPidDirectly(double p, double i, double d) {
        setPidDirectly(new XCANMotorControllerPIDProperties.Builder()
                .withP(p)
                .withI(i)
                .withD(d)
                .withVelocityFeedForward(0)
                .withGravityFeedForward(0)
                .build(), 0);
    }

    /**
     * Set the PID values for the motor controller directly, without using the property system.
     * This method sets the PID values on slot 0.
     * @param p The proportional gain to set.
     * @param i The integral gain to set.
     * @param d The derivative gain to set.
     * @param velocityFF The velocity feed forward to set.
     * @param gravityFF The gravity feed forward to set.
     */
    public void setPidDirectly(double p, double i, double d, double velocityFF, double gravityFF) {
        setPidDirectly(new XCANMotorControllerPIDProperties.Builder()
                .withP(p)
                .withI(i)
                .withD(d)
                .withVelocityFeedForward(velocityFF)
                .withGravityFeedForward(gravityFF)
                .build(), 0);
    }

    /**
     * Set the PID values for the motor controller directly, without using the property system.
     * @param p The proportional gain to set.
     * @param i The integral gain to set.
     * @param d The derivative gain to set.
     * @param staticFF The static feed forward to set.
     * @param velocityFF The velocity feed forward to set.
     * @param gravityFF The gravity feed forward to set.
     * @param slot The PID slot to set the values for.
     */
    public void setPidDirectly(double p, double i, double d, double staticFF, double velocityFF, double gravityFF, int slot) {
        setPidDirectly(new XCANMotorControllerPIDProperties.Builder()
                .withP(p)
                .withI(i)
                .withD(d)
                .withStaticFeedForward(staticFF)
                .withVelocityFeedForward(velocityFF)
                .withGravityFeedForward(gravityFF)
                .build(), slot);
    }

    /**
     * Set the PID values for the motor controller directly, without using the property system.
     * @param pidProperties The PID properties to set.
     * @param slot The PID slot to set the values for.
     */
    public abstract void setPidDirectly(XCANMotorControllerPIDProperties pidProperties, int slot);

    private void setAllPidValuesFromProperties() {
        if (usesPropertySystem) {
            setPowerRange(kMinOutputProps.get(), kMaxOutputProps.get());
            setVoltageRange(MAX_VOLTAGE.times(kMinOutputProps.get()),
                    MAX_VOLTAGE.times(kMaxOutputProps.get()));
            setPIDFromProperties();
        } else {
            log.warn("setAllProperties called on a Motor Controller that doesn't use the property system");
        }
    }

    private void setPIDFromProperties() {
        if (usesPropertySystem) {
            setPidDirectly(kPProps.get(currentPidSlot).get(), kIProps.get(currentPidSlot).get(), kDProps.get(currentPidSlot).get(),
                    kStaticFFProps.get(currentPidSlot).get(),
                    kVelocityFFProps.get(currentPidSlot).get(),
                    kGravityFFProps.get(currentPidSlot).get(),
                    currentPidSlot);
        } else {
            log.warn("setPIDFromProperties called on a Motor Controller that doesn't use the property system");
        }
    }

    private void validateSlot(int slot) {
        if (slot < 0 || slot >= totalPidSlot) {
            log.warn("Slot is not 0-4. Its now set to 0");
            currentPidSlot = 0;
        }
    }

    public abstract DeviceHealth getHealth();

    public void periodic() {
        var isUnhealthy = getHealth() == DeviceHealth.Unhealthy;
        unhealthyAlert.set(isUnhealthy);

        if (isUnhealthy) {
            // If the device is unhealthy none of the other periodic logic
            // will work, so we return early.
            return;
        }

        if (softwareForwardLimit.getAsBoolean() && getVoltage().gt(Volts.of(0))) {
            //log.warn("Forward software limit hit");
            setPower(0);
        }
        if (softwareReverseLimit.getAsBoolean() && getVoltage().lt(Volts.of(0))) {
            //log.warn("Reverse software limit hit");
            setPower(0);
        }

        if (usesPropertySystem) {
            if (firstPeriodicCall) {
                setAllPidValuesFromProperties();
                firstPeriodicCall = false;
            }

            // Since it costs the same to set all the PID values, we check to see if any have changed and then set them as a group.
            // In practice, it would be hard for a human to do this fast enough during tuning to really get any benefit, but it could happen
            // during some automated process.
            for (int slot = 0; slot < totalPidSlot; slot++) {
                if (LogicUtils.anyOf(
                        kPProps.get(slot).hasChangedSinceLastCheck(),
                        kIProps.get(slot).hasChangedSinceLastCheck(),
                        kDProps.get(slot).hasChangedSinceLastCheck(),
                        kStaticFFProps.get(slot).hasChangedSinceLastCheck(),
                        kVelocityFFProps.get(slot).hasChangedSinceLastCheck(),
                        kGravityFFProps.get(slot).hasChangedSinceLastCheck()))
                {

                    var pid = new XCANMotorControllerPIDProperties(
                            kPProps.get(slot).get(),
                            kIProps.get(slot).get(),
                            kDProps.get(slot).get(),
                            kStaticFFProps.get(slot).get(),
                            kVelocityFFProps.get(slot).get(),
                            kGravityFFProps.get(slot).get(),
                            kMaxOutputProps.get(),
                            kMinOutputProps.get()
                    );
                    pidProperties.put(slot, pid);

                    setPidDirectly(
                            pid.p(),
                            pid.i(),
                            pid.d(),
                            pid.staticFeedForward(),
                            pid.velocityFeedForward(),
                            pid.gravityFeedForward(),
                            slot
                    );
                }

            }

        }
        if (kMinOutputProps != null && kMaxOutputProps != null) {
            kMaxOutputProps.hasChangedSinceLastCheck((value) -> setPowerRange(kMinOutputProps.get(), value));
            kMinOutputProps.hasChangedSinceLastCheck((value) -> setPowerRange(value, kMaxOutputProps.get()));
        }
    }

    public abstract void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setTrapezoidalProfileAcceleration(AngularAcceleration acceleration);

    public abstract void setTrapezoidalProfileJerk(Velocity<AngularAccelerationUnit> jerk);

    public abstract void setTrapezoidalProfileMaxVelocity(AngularVelocity velocity);

    public abstract void setPower(double power);

    public abstract double getPower();

    public abstract void setPowerRange(double minPower, double maxPower);

    /**
     * Set the distance per motor rotation scaling factor for the motor controller.
     * This is used to convert the motor controller's position to a distance.
     * <p>Example: <code>setDistancePerMotorRotationScaleFactor(Meters.per(Rotation).of(0.5))</code></p>
     * @apiNote This is useful if you ever want to easily convert the motor controller's position to a distance
     * for calculating the position of a mechanism like an elevator.
     * @param distancePerAngle The distance per angle scaling factor to set.
     */
    public void setDistancePerMotorRotationsScaleFactor(Measure<? extends PerUnit<DistanceUnit, AngleUnit>> distancePerAngle) {
        this.distancePerMotorRotationsScaleFactor = distancePerAngle;
        this.distancePerMotorRotationsInverseScaleFactor = invertRatio(this.distancePerMotorRotationsScaleFactor);
    }

    /**
     * Set the angle scaling factor for the motor controller.
     * This is used to convert the motor controller's position to an angle.
     * <p>Example: <code>setAngleScaleFactor(Degrees.per(Rotation).of(488))</code></p>
     * @apiNote This is useful if you ever want to easily scale the reported angle of the motor controller,
     * like if you have some gearing on the output of the motor that directly affects the position of an arm.
     * @param angleScaleFactor The angle scaling factor to set.
     */
    public void setAngleScaleFactor(Measure<? extends PerUnit<AngleUnit, AngleUnit>> angleScaleFactor) {
        this.angleScaleFactor = angleScaleFactor;
        this.angleInverseScaleFactor = invertRatio(this.angleScaleFactor);
        this.angularVelocityScaleFactor = convertToAngularVelocity(angleScaleFactor);
        this.angularVelocityInverseScaleFactor = invertRatio(this.angularVelocityScaleFactor);
    }

    /**
     * Get the position reported by the motor controller.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     * @return The position reported by the motor controller.
     */
    public Angle getRawPosition() {
        return inputs.angle;
    }

    /**
     * Get the position reported by the motor controller.
     * @apiNote Distance per angle scaling factors configured on the motor controller are applied.
     * @return The position reported by the motor controller.
     */
    public Distance getPositionAsDistance() {
        return convertRawAngleToDistance(getRawPosition());
    }

    /**
     * Get the position reported by the motor controller.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     * @return The position reported by the motor controller.
     */
    public Angle getPosition() {
        return convertRawAngleToScaledAngle(getRawPosition());
    }

    /**
     * Override the position of the motor controller.
     * <p>Typically, this would be called to zero the reported position of the motor as part of a calibration routine.</p>
     * @param position The new position to set.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setPosition(Angle position) {
        setRawPosition(convertScaledAngleToRawAngle(position));
    }

    /**
     * Override the position of the motor controller.
     * <p>Typically, this would be called to zero the reported position of the motor as part of a calibration routine.</p>
     * @param position The new position to set.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public abstract void setRawPosition(Angle position);

    /**
     * Set the target position for the motor controller.
     * @param position The target position to set.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setPositionTarget(Angle position) {
        setPositionTarget(position, MotorPidMode.DutyCycle);
    }

    /**
     * Set the target position for the motor controller.
     * @param position The target position to set.
     * @param mode The PID mode to use when setting the target position.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setPositionTarget(Angle position, MotorPidMode mode) {
        setPositionTarget(position, mode, 0);
    }

    /**
     * Set the target position for the motor controller.
     * @param position The target position to set.
     * @param mode The PID mode to use when setting the target position.
     * @param slot The PID slot to use when setting the target position.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setPositionTarget(Angle position, MotorPidMode mode, int slot) {
        setRawPositionTarget(convertScaledAngleToRawAngle(position), mode, slot);
    }

    /**
     * Set the target position for the motor controller.
     * @param rawPosition The target position to set.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public void setRawPositionTarget(Angle rawPosition) {
        setRawPositionTarget(rawPosition, MotorPidMode.DutyCycle);
    }

    /**
     * Set the target position for the motor controller.
     * @param rawPosition The target position to set.
     * @param mode The PID mode to use when setting the target position.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public void setRawPositionTarget(Angle rawPosition, MotorPidMode mode) {
        setRawPositionTarget(rawPosition, mode, 0);
    }

    /**
     * Set the target position for the motor controller.
     * @param rawPosition The target position to set.
     * @param mode The PID mode to use when setting the target position.
     * @param slot The PID slot to use when setting the target position.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public abstract void setRawPositionTarget(Angle rawPosition, MotorPidMode mode, int slot);

    /**
     * Get the velocity reported by the motor controller.
     * @return The velocity reported by the motor controller.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public AngularVelocity getVelocity() {
        return convertRawVelocityToScaledVelocity(getRawVelocity());
    }

    /**
     * Get the velocity reported by the motor controller.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     * @return The velocity reported by the motor controller.
     */
    public AngularVelocity getRawVelocity() {
        return inputs.angularVelocity;
    }

    /**
     * Set the target velocity for the motor controller.
     * @param velocity The target velocity to set.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setVelocityTarget(AngularVelocity velocity) {
        setVelocityTarget(velocity, MotorPidMode.DutyCycle);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param velocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setVelocityTarget(AngularVelocity velocity, MotorPidMode mode) {
        setVelocityTarget(velocity, mode, 0);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param velocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @param slot The PID slot to use when setting the target velocity.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setVelocityTarget(AngularVelocity velocity, MotorPidMode mode, int slot) {
        setRawVelocityTarget(convertScaledVelocityToRawVelocity(velocity), mode, slot);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param rawVelocity The target velocity to set.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public void setRawVelocityTarget(AngularVelocity rawVelocity) {
        setRawVelocityTarget(rawVelocity, MotorPidMode.DutyCycle);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param rawVelocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public void setRawVelocityTarget(AngularVelocity rawVelocity, MotorPidMode mode) {
        setRawVelocityTarget(rawVelocity, mode, 0);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param rawVelocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @param slot The PID slot to use when setting the target velocity.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public abstract void setRawVelocityTarget(AngularVelocity rawVelocity, MotorPidMode mode, int slot);

    public abstract void setVoltage(Voltage voltage);

    public Voltage getVoltage() {
        return inputs.voltage;
    }

    public abstract void setVoltageRange(Voltage minVoltage, Voltage maxVoltage);

    public Current getCurrent() {
        return inputs.current;
    }

    public abstract boolean isInverted();

    protected abstract void updateInputs(XCANMotorControllerInputs inputs);

    public void refreshDataFrame() {
        updateInputs(inputs);
        Logger.processInputs(akitName, inputs);
    }

    protected boolean isValidVoltageRequest(Voltage voltage) {
        if (voltage.gt(Volts.of(0)) && softwareForwardLimit.getAsBoolean()) {
            // TODO: Change these various warnings to only trigger once on the rising edge of the issue.
            //log.warn("Attempted to set positive voltage on motor controller with forward software limit enabled");
            return false;
        }
        if (voltage.lt(Volts.of(0)) && softwareReverseLimit.getAsBoolean()) {
            //log.warn("Attempted to set negative voltage on motor controller with reverse software limit enabled");
            return false;
        }
        return true;
    }

    protected boolean isValidPowerRequest(double power) {
        if (power > 0 && softwareForwardLimit.getAsBoolean()) {
            //log.warn("Attempted to set positive power on motor controller with forward software limit enabled");
            return false;
        }
        if (power < 0 && softwareReverseLimit.getAsBoolean()) {
            //log.warn("Attempted to set negative power on motor controller with reverse software limit enabled");
            return false;
        }
        return true;
    }

    protected Angle convertRawAngleToScaledAngle(Angle rawAngle) {
        if (angleScaleFactor == null) {
            return rawAngle;
        }
        return rawAngle.timesConversionFactor(angleScaleFactor);
    }

    protected Distance convertRawAngleToDistance(Angle rawAngle) {
        if (distancePerMotorRotationsScaleFactor == null) {
            //log.warn("Distance per angle not set for motor controller {}", akitName);
            return Meters.zero();
        }
        return rawAngle.timesConversionFactor(distancePerMotorRotationsScaleFactor);
    }

    protected Angle convertDistanceToRawAngle(Distance distance) {
        if (distancePerMotorRotationsInverseScaleFactor == null) {
            //log.warn("Distance per angle not set for motor controller {}", akitName);
            return Rotations.zero();
        }
        return distance.timesConversionFactor(distancePerMotorRotationsInverseScaleFactor);
    }

    protected Angle convertScaledAngleToRawAngle(Angle scaledAngle) {
        if (angleScaleFactor == null) {
            return scaledAngle;
        }
        return scaledAngle.timesConversionFactor(angleInverseScaleFactor);
    }

    protected AngularVelocity convertRawVelocityToScaledVelocity(AngularVelocity rawVelocity) {
        if (angularVelocityScaleFactor == null) {
            return rawVelocity;
        }
        return rawVelocity.timesConversionFactor(angularVelocityScaleFactor);
    }

    protected AngularVelocity convertScaledVelocityToRawVelocity(AngularVelocity scaledVelocity) {
        if (angularVelocityInverseScaleFactor == null) {
            return scaledVelocity;
        }
        return scaledVelocity.timesConversionFactor(angularVelocityInverseScaleFactor);
    }

    private <N extends Unit, D extends Unit> Measure<? extends PerUnit<D, N>> invertRatio(Measure<? extends PerUnit<N, D>> ratio) {
        if (ratio == null) {
            return null;
        }

        return ratio.unit().reciprocal().of(1 / ratio.magnitude());
    }

    private Measure<? extends PerUnit<AngularVelocityUnit, AngularVelocityUnit>> convertToAngularVelocity(Measure<? extends PerUnit<AngleUnit,
            AngleUnit>> base) {
        if (base == null) {
            return null;
        }

        var magnitude = base.magnitude();
        var unit = base.unit();
        var numeratorUnit = unit.numerator().per(Second);
        var denominatorUnit = unit.denominator().per(Second);
        return AngularVelocityUnit.combine(numeratorUnit, denominatorUnit).of(magnitude);
    }

    public abstract void setPositionAndVelocityUpdateFrequency(Frequency frequency);
}
