package xbot.common.controls.actuators;

import edu.wpi.first.units.AngularAccelerationUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import org.apache.logging.log4j.LogManager;
import org.littletonrobotics.junction.Logger;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.controls.io_inputs.XCANMotorControllerInputsAutoLogged;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.logic.LogicUtils;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import java.util.function.Supplier;

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

    protected Supplier<Boolean> softwareReverseLimit = () -> false;
    protected Supplier<Boolean> softwareForwardLimit = () -> false;

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
        this.akitName = info.name()+"CANMotorController";

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

    public void periodic() {
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

    public Angle getPosition() {
        return inputs.angle;
    }

    public void setPosition(Angle position) {
        setPositionTarget(position, MotorPidMode.DutyCycle);
    }

    public void setPositionTarget(Angle position, MotorPidMode mode) {
        setPositionTarget(position, mode, 0);
    }

    public abstract void setPositionTarget(Angle position, MotorPidMode mode, int slot);

    public AngularVelocity getVelocity() {
        return inputs.angularVelocity;
    }

    public void setVelocityTarget(AngularVelocity velocity) {
        setVelocityTarget(velocity, MotorPidMode.DutyCycle);
    }

    public void setVelocityTarget(AngularVelocity velocity, MotorPidMode mode) {
        setVelocityTarget(velocity, mode, 0);
    }

    public abstract void setVelocityTarget(AngularVelocity velocity, MotorPidMode mode, int slot);

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
}
