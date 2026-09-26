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
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import edu.wpi.first.units.measure.Voltage;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.PowerDistributionProperties;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Rotations;

/**
 * Mock XCANMotorController for simulation.
 * - Power and voltage is grouped.
 * - The user is responsible for updating its position and velocity.
 */
public class MockCANMotorController extends XCANMotorController {
    public enum ControlMode {
        DutyCycle,
        Position,
        Velocity
    }

    private ControlMode controlMode = ControlMode.DutyCycle;
    private double power = 0.0;
    private Voltage voltage = Volts.zero();
    private Current current = Amps.zero();
    private Angle position = Rotations.zero();
    private Angle targetPosition = Rotations.zero();
    private AngularVelocity targetVelocity = RPM.zero();
    private AngularVelocity velocity = RPM.zero();
    public double p;
    public double i;
    public double d;
    public double s;
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
            @Assisted("defaultPIDProperties") XCANMotorControllerPIDProperties defaultPIDProperties,
            DataFrameRegistry dataFrameRegistry,
            PowerDistributionProperties pdProperties
    ) {
        super(info, owningSystemPrefix, propertyFactory, police, pidPropertyPrefix, defaultPIDProperties, dataFrameRegistry, pdProperties);
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
    public void setTrapezoidalProfileMaxVelocity(AngularVelocity velocity) {

    }

    @Override
    public void setPidDirectly(XCANMotorControllerPIDProperties pidProperties, int slot) {
        this.p = pidProperties.p();
        this.i = pidProperties.i();
        this.d = pidProperties.d();
        this.s = pidProperties.staticFeedForward();
        this.f = pidProperties.velocityFeedForward();
        this.g = pidProperties.gravityFeedForward();
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
        this.voltage = Volts.of(MathUtil.clamp(power * 12.0, -12.0, 12.0));
        this.current = Amps.of(MathUtil.clamp(power, -1.0, 1.0));
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

    public Angle getRawPosition_internal() {
        return this.position.copy();
    }

    @Override
    public void setRawPosition(Angle position) {
        this.position = position;
    }

    @Override
    public void setRawPositionTarget(Angle rawPosition, MotorPidMode mode, int slot) {
        controlMode = ControlMode.Position;
        this.targetPosition = rawPosition;
    }

    public Angle getTargetPosition() {
        return convertRawAngleToScaledAngle(targetPosition);
    }

    public Angle getRawTargetPosition() {
        return targetPosition.copy();
    }

    public AngularVelocity getRawVelocity_internal() {
        return velocity.copy();
    }

    public void setVelocity(AngularVelocity velocity) {
        this.velocity = convertScaledVelocityToRawVelocity(velocity);
    }

    public void setRawVelocity(AngularVelocity rawVelocity) {
        this.velocity = rawVelocity;
    }

    @Override
    public void setRawVelocityTarget(AngularVelocity rawVelocity, MotorPidMode mode, int slot) {
        controlMode = ControlMode.Velocity;
        this.targetVelocity = rawVelocity;
    }

    @Override
    public void setRawVelocityTargetWithFeedForward(AngularVelocity rawVelocity, MotorPidMode mode, double feedForward, int slot) {
        setRawVelocityTarget(rawVelocity, mode, slot);
    }

    public AngularVelocity getRawTargetVelocity() {
        return targetVelocity.copy();
    }

    @Override
    public void setVoltage(Voltage voltage) {
        if (!isValidVoltageRequest(voltage)) {
            return;
        }
        this.voltage = voltage;
        this.power = MathUtil.clamp(voltage.in(Volts) / 12.0, -1.0, 1.0);
        this.current = Amps.of(voltage.in(Volts) / 12.0);
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
        inputs.angle = getRawPosition_internal();
        inputs.angularVelocity = getRawVelocity_internal();
        inputs.voltage = voltage;
        inputs.current = current;
    }
}
