package xbot.common.controls.actuators.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.AngularAccelerationUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularAcceleration;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Velocity;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.PropertyFactory;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volts;
import static edu.wpi.first.units.Units.Rotations;

public class MockCANMotorController extends XCANMotorController {

    private double power = 0.0;
    private Angle position = Rotations.zero();
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
    public void setPower(double power) {
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
    public Angle getPosition() {
        return this.position;
    }

    @Override
    public void setPosition(Angle position) {
        this.position = position;
    }

    @Override
    public void setPositionTarget(Angle position, MotorPidMode mode, int slot) {

    }

    @Override
    public AngularVelocity getVelocity() {
        return RPM.zero();
    }

    @Override
    public void setVelocityTarget(AngularVelocity velocity, MotorPidMode mode, int slot) {

    }

    @Override
    public boolean isInverted() {
        return false;
    }

    @Override
    protected void updateInputs(XCANMotorControllerInputs inputs) {
        inputs.angle = getPosition();
        inputs.angularVelocity = getVelocity();
        inputs.voltage = Volts.of(power * 12);
        inputs.current = Amps.of(power * 1);
    }
}
