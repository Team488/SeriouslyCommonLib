package xbot.common.controls.actuators;

import org.wpilib.units.AngleUnit;
import org.wpilib.units.AngularAccelerationUnit;
import org.wpilib.units.AngularVelocityUnit;
import org.wpilib.units.DistanceUnit;
import org.wpilib.units.Measure;
import org.wpilib.units.PerUnit;
import org.wpilib.units.Unit;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularAcceleration;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Current;
import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.Frequency;
import org.wpilib.units.measure.Time;
import org.wpilib.units.measure.Velocity;
import org.wpilib.units.measure.Voltage;
import org.wpilib.util.Alert;
import org.wpilib.tunable.TunableDouble;
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
import xbot.common.properties.PowerDistributionProperties;
import xbot.common.properties.TunableFactory;
import xbot.common.resiliency.DeviceHealth;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BooleanSupplier;

import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.Second;
import static org.wpilib.units.Units.Volts;

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
                String pidTunablePrefix,
                XCANMotorControllerPIDProperties defaultPIDProperties
        );

        default XCANMotorController create(
                CANMotorControllerInfo info,
                String owningSystemPrefix,
                String pidTunablePrefix
        ) {
            return create(info, owningSystemPrefix, pidTunablePrefix, null);
        }
    }

    /**
     * The maximum voltage that the motor controller can accept.
     * This is typically 12V, but at the start of a match the voltage could be a little higher..
     */
    public static final Voltage MAX_VOLTAGE = Volts.of(13.8);

    public final CANBusId busId;
    public final int deviceId;
    public final TunableFactory tunableFactory;
    private final String defaultTunablePrefix;
    private final String pidTunablePrefix;

    protected Map<Integer, XCANMotorControllerPIDProperties> pidProperties = new HashMap<>();

    protected XCANMotorControllerInputsAutoLogged inputs;
    protected String akitName;

    protected boolean usesTunables = true;
    protected boolean firstPeriodicCall = true;

    protected Map<Integer, TunableDouble> kPTunables = new HashMap<>();
    protected Map<Integer, TunableDouble> kITunables = new HashMap<>();
    protected Map<Integer, TunableDouble> kDTunables = new HashMap<>();
    protected Map<Integer, TunableDouble> kStaticFFTunables = new HashMap<>();
    protected Map<Integer, TunableDouble> kVelocityFFTunables = new HashMap<>();
    protected Map<Integer, TunableDouble> kGravityFFTunables = new HashMap<>();
    protected TunableDouble kMaxOutputTunable;
    protected TunableDouble kMinOutputTunable;
    private Double lastAppliedMaxOutput;
    private Double lastAppliedMinOutput;

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(XCANMotorController.class);
    private static final AtomicLong NEXT_ALERT_ID = new AtomicLong();

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
            TunableFactory tunableFactory,
            DevicePolice police,
            String pidTunablePrefix,
            XCANMotorControllerPIDProperties defaultPIDProperties,
            DataFrameRegistry dataFrameRegistry,
            PowerDistributionProperties pdProperties
    ) {
        this.busId = info.busId();
        this.deviceId = info.deviceId();
        this.tunableFactory = tunableFactory;
        pdProperties.setDeviceMapping(info.pdhPort(), info.name());

        this.inputs = new XCANMotorControllerInputsAutoLogged();

        this.defaultTunablePrefix = owningSystemPrefix + "/" + info.name();
        this.pidTunablePrefix = owningSystemPrefix + "/" + pidTunablePrefix;

        this.tunableFactory.setPrefix(defaultTunablePrefix);

        police.registerDevice(DevicePolice.DeviceType.CAN, busId, info.deviceId(), info.name());
        this.akitName = info.name()+"/CANMotorController";

        String alertId = getClass().getName() + "-" + busId.id() + "-" + info.deviceId() + "-" + NEXT_ALERT_ID.getAndIncrement();
        this.unhealthyAlert = new Alert(AlertGroups.DEVICE_HEALTH, alertId,
                "Motor Controller " + info.deviceId() + " on CAN bus " + busId + " (" + owningSystemPrefix + ") is unhealthy",
                Alert.Level.HIGH);

        if (defaultPIDProperties == null) {
            // If the controller wasn't given a default PID configuration, we shouldn't create
            // matching P/I/D/F/... tunables. Tunables do have a small performance cost to the robot,
            // and we typically only need them for a subset of motor controllers on the robot - simpler
            // "open loop" controllers don't need them.
            usesTunables = false;
        } else  {
            // Min/max output are not settable via slots on our primary motor controller type
            tunableFactory.setPrefix(this.pidTunablePrefix);
            kMaxOutputTunable = tunableFactory.createDouble("kMaxOutput", defaultPIDProperties.maxPowerOutput());
            kMinOutputTunable = tunableFactory.createDouble("kMinOutput", defaultPIDProperties.minPowerOutput());

            for (int slot = 0; slot < totalPidSlot; slot++) {
                tunableFactory.setPrefix(this.pidTunablePrefix + "/" + slot);

                kPTunables.put(slot, tunableFactory.createDouble("kP", defaultPIDProperties.p()));
                kITunables.put(slot, tunableFactory.createDouble("kI", defaultPIDProperties.i()));
                kDTunables.put(slot, tunableFactory.createDouble("kD", defaultPIDProperties.d()));
                kStaticFFTunables.put(slot, tunableFactory.createDouble("kStaticFeedForward", defaultPIDProperties.staticFeedForward()));
                kVelocityFFTunables.put(slot, tunableFactory.createDouble("kVelocityFeedForward", defaultPIDProperties.velocityFeedForward()));
                kGravityFFTunables.put(slot, tunableFactory.createDouble("kGravityFeedForward", defaultPIDProperties.gravityFeedForward()));

            }
            this.tunableFactory.setPrefix(this.defaultTunablePrefix);
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
     * Set the PID values for the motor controller directly, without using tunables.
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
     * Set the PID values for the motor controller directly, without using tunables.
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
     * Set the PID values for the motor controller directly, without using tunables.
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
     * Set the PID values for the motor controller directly, without using tunables.
     * @param pidProperties The PID properties to set.
     * @param slot The PID slot to set the values for.
     */
    public abstract void setPidDirectly(XCANMotorControllerPIDProperties pidProperties, int slot);

    private void setAllPidValuesFromTunables() {
        if (usesTunables) {
            double minOutput = kMinOutputTunable.get();
            double maxOutput = kMaxOutputTunable.get();
            setPowerRange(minOutput, maxOutput);
            setVoltageRange(MAX_VOLTAGE.times(minOutput), MAX_VOLTAGE.times(maxOutput));
            lastAppliedMinOutput = minOutput;
            lastAppliedMaxOutput = maxOutput;

            for (int slot = 0; slot < totalPidSlot; slot++) {
                setPIDFromTunables(slot);
            }
        } else {
            log.warn("setAllPidValuesFromTunables called on a Motor Controller that doesn't use tunables");
        }
    }

    private void setPIDFromTunables(int slot) {
        if (usesTunables) {
            var pid = getPIDFromTunables(slot);
            pidProperties.put(slot, pid);
            setPidDirectly(pid, slot);
        } else {
            log.warn("setPIDFromTunables called on a Motor Controller that doesn't use tunables");
        }
    }

    private XCANMotorControllerPIDProperties getPIDFromTunables(int slot) {
        return new XCANMotorControllerPIDProperties(
                kPTunables.get(slot).get(),
                kITunables.get(slot).get(),
                kDTunables.get(slot).get(),
                kStaticFFTunables.get(slot).get(),
                kVelocityFFTunables.get(slot).get(),
                kGravityFFTunables.get(slot).get(),
                kMaxOutputTunable.get(),
                kMinOutputTunable.get());
    }

    private boolean pidValuesDiffer(
            XCANMotorControllerPIDProperties current,
            XCANMotorControllerPIDProperties lastApplied) {
        if (lastApplied == null) {
            return true;
        }
        if (Double.compare(current.p(), lastApplied.p()) != 0
                || Double.compare(current.i(), lastApplied.i()) != 0
                || Double.compare(current.d(), lastApplied.d()) != 0) {
            return true;
        }
        return Double.compare(current.staticFeedForward(), lastApplied.staticFeedForward()) != 0
                || Double.compare(current.velocityFeedForward(), lastApplied.velocityFeedForward()) != 0
                || Double.compare(current.gravityFeedForward(), lastApplied.gravityFeedForward()) != 0;
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

        if (softwareForwardLimit.getAsBoolean() && getVoltage().gt(Volts.zero())) {
            //log.warn("Forward software limit hit");
            setPower(0);
        }
        if (softwareReverseLimit.getAsBoolean() && getVoltage().lt(Volts.zero())) {
            //log.warn("Reverse software limit hit");
            setPower(0);
        }

        if (usesTunables) {
            if (firstPeriodicCall) {
                setAllPidValuesFromTunables();
                firstPeriodicCall = false;
            }

            // Since it costs the same to set all the PID values, we check to see if any have changed and then set them as a group.
            // In practice, it would be hard for a human to do this fast enough during tuning to really get any benefit, but it could happen
            // during some automated process.
            for (int slot = 0; slot < totalPidSlot; slot++) {
                var pid = getPIDFromTunables(slot);
                if (pidValuesDiffer(pid, pidProperties.get(slot))) {
                    pidProperties.put(slot, pid);
                    setPidDirectly(pid, slot);
                }
            }

        }
        if (kMinOutputTunable != null && kMaxOutputTunable != null) {
            double maxOutput = kMaxOutputTunable.get();
            double minOutput = kMinOutputTunable.get();
            if (lastAppliedMaxOutput == null || Double.compare(maxOutput, lastAppliedMaxOutput) != 0) {
                setPowerRange(minOutput, maxOutput);
                lastAppliedMaxOutput = maxOutput;
            }
            if (lastAppliedMinOutput == null || Double.compare(minOutput, lastAppliedMinOutput) != 0) {
                setPowerRange(minOutput, maxOutput);
                lastAppliedMinOutput = minOutput;
            }
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

    /**
     * Set the target velocity for the motor controller.
     * @param velocity The target velocity to set.
     * @param feedForward The additional feed forward to apply (-1..1 for duty-cycle PID mode).
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setVelocityTargetWithFeedForward(AngularVelocity velocity, double feedForward) {
        setVelocityTargetWithFeedForward(velocity, MotorPidMode.DutyCycle, feedForward);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param velocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @param feedForward The additional feed forward to apply (in volts for voltage PID modes or -1..1 for duty-cycle PID modes).
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setVelocityTargetWithFeedForward(AngularVelocity velocity, MotorPidMode mode, double feedForward) {
        setVelocityTargetWithFeedForward(velocity, mode, feedForward,0);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param velocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @param feedForward The additional feed forward to apply (in volts for voltage PID modes or -1..1 for duty-cycle PID modes).
     * @param slot The PID slot to use when setting the target velocity.
     * @apiNote Angle scaling factors configured on the motor controller are applied.
     */
    public void setVelocityTargetWithFeedForward(AngularVelocity velocity, MotorPidMode mode, double feedForward, int slot) {
        setRawVelocityTargetWithFeedForward(convertScaledVelocityToRawVelocity(velocity), mode, feedForward, slot);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param rawVelocity The target velocity to set.
     * @param feedForward The additional feed forward to apply (-1..1 for duty-cycle PID mode).
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public void setRawVelocityTargetWithFeedForward(AngularVelocity rawVelocity, double feedForward) {
        setRawVelocityTargetWithFeedForward(rawVelocity, MotorPidMode.DutyCycle, feedForward);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param rawVelocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @param feedForward The additional feed forward to apply (in volts for voltage PID modes or -1..1 for duty-cycle PID modes).
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public void setRawVelocityTargetWithFeedForward(AngularVelocity rawVelocity, MotorPidMode mode, double feedForward) {
        setRawVelocityTargetWithFeedForward(rawVelocity, mode, feedForward, 0);
    }

    /**
     * Set the target velocity for the motor controller.
     * @param rawVelocity The target velocity to set.
     * @param mode The PID mode to use when setting the target velocity.
     * @param feedForward The additional feed forward to apply (in volts for voltage PID modes or -1..1 for duty-cycle PID modes).
     * @param slot The PID slot to use when setting the target velocity.
     * @apiNote Angle scaling factors configured on the motor controller are ignored.
     */
    public abstract void setRawVelocityTargetWithFeedForward(AngularVelocity rawVelocity, MotorPidMode mode, double feedForward, int slot);

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
        if (voltage.gt(Volts.zero()) && softwareForwardLimit.getAsBoolean()) {
            // TODO: Change these various warnings to only trigger once on the rising edge of the issue.
            //log.warn("Attempted to set positive voltage on motor controller with forward software limit enabled");
            return false;
        }
        if (voltage.lt(Volts.zero()) && softwareReverseLimit.getAsBoolean()) {
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
