package xbot.common.controls.actuators.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.AngularAccelerationUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Frequency;
import edu.wpi.first.units.measure.MutAngle;
import edu.wpi.first.units.measure.MutAngularVelocity;
import edu.wpi.first.units.measure.MutCurrent;
import edu.wpi.first.units.measure.MutVoltage;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volt;
import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Rotations;

public class MockCANMotorController extends XCANMotorController {
    public enum ControlMode {
        DutyCycle,
        Position,
        Velocity
    }

    private ControlMode controlMode = ControlMode.DutyCycle;
    private double power = 0.0;
    private final MutVoltage voltage = Volts.mutable(0);
    private final MutCurrent current = Amps.mutable(0);
    private final MutAngle position = Rotations.mutable(0);
    private final MutAngle targetPosition = Rotations.mutable(0);
    private final MutAngularVelocity targetVelocity = RPM.mutable(0);
    private final MutAngularVelocity velocity = RPM.mutable(0);
    public double p;
    public double i;
    public double d;
    public double f;
    public double g;
    public double maxPower;
    public double minPower;

    @AssistedFactory
    public abstract static class MockCANMotorControllerFactory implements XCANMotorControllerFactory {
        public abstract MockCANMotorController create(
                @Assisted("info") CANMotorControllerInfo info,
                @Assisted("owningSystemPrefix") String owningSystemPrefix,
                @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
                @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties);
    }

    @AssistedInject
    public MockCANMotorController(
            @Assisted("info") CANMotorControllerInfo info,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            PropertyFactory propertyFactory,
            DevicePolice police,
            @Assisted("pidPropertyPrefix") String pidPropertyPrefix,
            @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties
    ) {
        super(info, owningSystemPrefix, propertyFactory, police, pidPropertyPrefix, defaultPIDProperties);
    }

    @Override
    public void setConfiguration(CANMotorControllerOutputConfig outputConfig) {

    }

    @Override
    public void setOpenLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {

    }

    @Override
    public void setClosedLoopRampRates(Time dutyCyclePeriod, Time voltagePeriod) {

    }

    @Override
    public void setTrapezoidalProfileAcceleration(AngularAcceleration acceleration) {

    }

    @Override
    public void setTrapezoidalProfileJerk(Velocity<AngularAccelerationUnit> jerk) {

    }

    @Override
    public void setPidDirectly(double p, double i, double d, double velocityFF, double gravityFF, int slot) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = velocityFF;
        this.g = gravityFF;
    }

    @Override
    public DeviceHealth getHealth() {
        return DeviceHealth.Healthy;
    }

    @Override
    public void setPower(double power) {
        if (!isValidPowerRequest(power)) {
            return;
        }
        controlMode = ControlMode.DutyCycle;
        this.power = MathUtil.clamp(power, -1.0, 1.0);
        this.voltage.mut_replace(MathUtil.clamp(power * 12.0, -12.0, 12.0), Volts);
        this.current.mut_replace(MathUtil.clamp(power, -1.0, 1.0), Amps);
    }

    /*
     * Set the internal power of the motor controller without changing the controlMode.
     * Useful for simulating an internal pid on a motor controller.
     */
    public void setPowerInternal(double power) {
        this.power = MathUtil.clamp(power, -1.0, 1.0);
    }

    @Override
    public double getPower() {
        return this.power;
    }

    @Override
    public void setPowerRange(double minPower, double maxPower) {
        this.minPower = minPower;
        this.maxPower = maxPower;
    }

    @Override
    public Angle getRawPosition() {
        return this.position.copy();
    }

    @Override
    public void setRawPosition(Angle position) {
        this.position.mut_replace(position);
    }

    @Override
    public void setRawPositionTarget(Angle rawPosition, MotorPidMode mode, int slot) {
        controlMode = ControlMode.Position;
        this.targetPosition.mut_replace(rawPosition);
    }

    public Angle getTargetPosition() {
        return convertRawAngleToScaledAngle(targetPosition);
    }

    public Angle getRawTargetPosition() {
        return targetPosition.copy();
    }

    @Override
    public AngularVelocity getRawVelocity() {
        return velocity.copy();
    }

    public void setVelocity(AngularVelocity velocity) {
        this.velocity.mut_replace(convertScaledVelocityToRawVelocity(velocity));
    }

    public void setRawVelocity(AngularVelocity rawVelocity) {
        this.velocity.mut_replace(rawVelocity);
    }

    @Override
    public void setRawVelocityTarget(AngularVelocity rawVelocity, MotorPidMode mode, int slot) {
        controlMode = ControlMode.Velocity;
        this.targetVelocity.mut_replace(rawVelocity);
    }

    public AngularVelocity getRawTargetVelocity() {
        return targetVelocity.copy();
    }

    @Override
    public void setVoltage(Voltage voltage) {
        if (!isValidVoltageRequest(voltage)) {
            return;
        }
        this.voltage.mut_replace(voltage);
        this.power = MathUtil.clamp(voltage.in(Volts) / 12.0, -1.0, 1.0);
        this.current.mut_replace(voltage.in(Volts) / 12.0, Amps);
    }

    @Override
    public void setVoltageRange(Voltage minVoltage, Voltage maxVoltage) {
    }

    @Override
    public boolean isInverted() {
        return false;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    @Override
    public void setPositionAndVelocityUpdateFrequency(Frequency frequency) {
        // Do nothing
    }

    @Override
    protected void updateInputs(XCANMotorControllerInputs inputs) {
        inputs.angle = getPosition();
        inputs.angularVelocity = getVelocity();
        inputs.voltage = voltage;
        inputs.current = current;
    }
}
