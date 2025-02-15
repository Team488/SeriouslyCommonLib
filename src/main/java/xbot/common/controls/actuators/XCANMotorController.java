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
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.Alert;
import org.apache.logging.log4j.LogManager;
import org.littletonrobotics.junction.Logger;

import xbot.common.advantage.DataFrameRefreshable;
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

import java.util.function.Supplier;

import static edu.wpi.first.units.Units.Meters;
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
            return create(info, owningSystemPrefix, pidPropertyPrefix, new XCANMotorControllerPIDProperties());
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

    protected XCANMotorControllerInputsAutoLogged inputs;
    protected String akitName;

    protected boolean usesPropertySystem = true;
    protected boolean firstPeriodicCall = true;

    protected DoubleProperty kPProp;
    protected DoubleProperty kIProp;
    protected DoubleProperty kDProp;
    protected DoubleProperty kVelocityFFProp;
    protected DoubleProperty kGravityFFProp;
    protected DoubleProperty kMaxOutputProp;
    protected DoubleProperty kMinOutputProp;

    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(XCANMotorController.class);

    protected Measure<? extends PerUnit<DistanceUnit, AngleUnit>> distancePerMotorRotationsScaleFactor;
    protected Measure<? extends PerUnit<AngleUnit, AngleUnit>> angleScaleFactor;

    protected Supplier<Boolean> softwareReverseLimit = () -> false;
    protected Supplier<Boolean> softwareForwardLimit = () -> false;

    private final Alert unhealthyAlert;

    protected XCANMotorController(
            CANMotorControllerInfo info,
            String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            String pidPropertyPrefix,
            XCANMotorControllerPIDProperties defaultPIDProperties
    ) {
        this.busId = info.busId();
        this.deviceId = info.deviceId();
        this.propertyFactory = propertyFactory;

        this.inputs = new XCANMotorControllerInputsAutoLogged();

        this.propertyFactory.setPrefix(owningSystemPrefix + "/" + info.name());
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
        } else {
            this.propertyFactory.setPrefix(pidPropertyPrefix);
            kPProp = propertyFactory.createPersistentProperty("kP", defaultPIDProperties.p());
            kIProp = propertyFactory.createPersistentProperty("kI", defaultPIDProperties.i());
            kDProp = propertyFactory.createPersistentProperty("kD", defaultPIDProperties.d());
            kVelocityFFProp = propertyFactory.createPersistentProperty("kVelocityFeedForward", defaultPIDProperties.velocityFeedForward());
            kGravityFFProp = propertyFactory.createPersistentProperty("kGravityFeedForward", defaultPIDProperties.gravityFeedForward());
            kMaxOutputProp = propertyFactory.createPersistentProperty("kMaxOutput", defaultPIDProperties.maxPowerOutput());
            kMinOutputProp = propertyFactory.createPersistentProperty("kMinOutput", defaultPIDProperties.minPowerOutput());
        }
    }

    public abstract void setConfiguration(CANMotorControllerOutputConfig outputConfig);

    /**
     * Set the software forward limit for the motor controller.
     * If the limit is hit, the motor controller will stop.
     * This is evaluated on every periodic call.
     * @param softwareForwardLimit A supplier that returns true if the forward limit is hit.
     */
    public void setSoftwareForwardLimit(Supplier<Boolean> softwareForwardLimit) {
        this.softwareForwardLimit = softwareForwardLimit;
    }

    /**
     * Set the software reverse limit for the motor controller.
     * If the limit is hit, the motor controller will stop.
     * This is evaluated on every periodic call.
     * @param softwareReverseLimit A supplier that returns true if the reverse limit is hit.
     */
    public void setSoftwareReverseLimit(Supplier<Boolean> softwareReverseLimit) {
        this.softwareReverseLimit = softwareReverseLimit;
    }

    public void setPidDirectly(double p, double i, double d) {
        setPidDirectly(p, i, d, 0, 0);
    }

    public void setPidDirectly(double p, double i, double d, double velocityFF, double gravityFF) {
        setPidDirectly(p, i, d, velocityFF, gravityFF, 0);
    }

    public abstract void setPidDirectly(double p, double i, double d, double velocityFF, double gravityFF, int slot);

    private void setAllPidValuesFromProperties() {
        if (usesPropertySystem) {
            setPowerRange(kMinOutputProp.get(), kMaxOutputProp.get());
            setVoltageRange(MAX_VOLTAGE.times(kMinOutputProp.get()), MAX_VOLTAGE.times(kMaxOutputProp.get()));
            setPIDFromProperties();
        } else {
            log.warn("setAllProperties called on a Motor Controller that doesn't use the property system");
        }
    }

    private void setPIDFromProperties() {
        if (usesPropertySystem) {
            setPidDirectly(kPProp.get(), kIProp.get(), kDProp.get(), kVelocityFFProp.get(), kGravityFFProp.get());
        } else {
            log.warn("setPIDFromProperties called on a Motor Controller that doesn't use the property system");
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

        if (softwareForwardLimit.get() && getVoltage().gt(Volts.of(0))) {
            log.warn("Forward software limit hit");
            setPower(0);
        }
        if (softwareReverseLimit.get() && getVoltage().lt(Volts.of(0))) {
            log.warn("Reverse software limit hit");
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
            if (LogicUtils.anyOf(
                    kPProp.hasChangedSinceLastCheck(),
                    kIProp.hasChangedSinceLastCheck(),
                    kDProp.hasChangedSinceLastCheck(),
                    kVelocityFFProp.hasChangedSinceLastCheck(),
                    kGravityFFProp.hasChangedSinceLastCheck()))
            {
                setPIDFromProperties();
            }

            kMaxOutputProp.hasChangedSinceLastCheck((value) -> setPowerRange(kMinOutputProp.get(), value));
            kMinOutputProp.hasChangedSinceLastCheck((value) -> setPowerRange(value, kMaxOutputProp.get()));
        }
    }

    public abstract void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod);

    public abstract void setTrapezoidalProfileAcceleration(AngularAcceleration acceleration);

    public abstract void setTrapezoidalProfileJerk(Velocity<AngularAccelerationUnit> jerk);

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
        if (voltage.gt(Volts.of(0)) && softwareForwardLimit.get()) {
            log.warn("Attempted to set positive voltage on motor controller with forward software limit enabled");
            return false;
        }
        if (voltage.lt(Volts.of(0)) && softwareReverseLimit.get()) {
            log.warn("Attempted to set negative voltage on motor controller with reverse software limit enabled");
            return false;
        }
        return true;
    }

    protected boolean isValidPowerRequest(double power) {
        if (power > 0 && softwareForwardLimit.get()) {
            log.warn("Attempted to set positive power on motor controller with forward software limit enabled");
            return false;
        }
        if (power < 0 && softwareReverseLimit.get()) {
            log.warn("Attempted to set negative power on motor controller with reverse software limit enabled");
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
            log.warn("Distance per angle not set for motor controller {}", akitName);
            return Meters.zero();
        }
        return rawAngle.timesConversionFactor(distancePerMotorRotationsScaleFactor);
    }

    protected Angle convertScaledAngleToRawAngle(Angle scaledAngle) {
        if (angleScaleFactor == null) {
            return scaledAngle;
        }
        return scaledAngle.timesConversionFactor(invertRatio(angleScaleFactor));
    }

    protected AngularVelocity convertRawVelocityToScaledVelocity(AngularVelocity rawVelocity) {
        if (angleScaleFactor == null) {
            return rawVelocity;
        }
        return rawVelocity.timesConversionFactor(convertToAngularVelocity(angleScaleFactor));
    }

    protected AngularVelocity convertScaledVelocityToRawVelocity(AngularVelocity scaledVelocity) {
        if (angleScaleFactor == null) {
            return scaledVelocity;
        }
        return scaledVelocity.timesConversionFactor(convertToAngularVelocity(invertRatio(angleScaleFactor)));
    }

    protected <N extends Unit, D extends Unit> Measure<? extends PerUnit<D, N>> invertRatio(Measure<? extends PerUnit<N, D>> ratio) {
        return ratio.unit().reciprocal().of(1 / ratio.magnitude());
    }

    protected Measure<? extends PerUnit<AngularVelocityUnit, AngularVelocityUnit>> convertToAngularVelocity(Measure<? extends PerUnit<AngleUnit,
                                                                                                            AngleUnit>> base) {
        var magnitude = base.magnitude();
        var unit = base.unit();
        var numeratorUnit = unit.numerator().per(Second);
        var denominatorUnit = unit.denominator().per(Second);
        return AngularVelocityUnit.combine(numeratorUnit, denominatorUnit).of(magnitude);
    }
}
