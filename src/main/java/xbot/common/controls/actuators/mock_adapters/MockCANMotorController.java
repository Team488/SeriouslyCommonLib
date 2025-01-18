package xbot.common.controls.actuators.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Time;
import xbot.common.controls.actuators.XCANMotorController;
import xbot.common.controls.actuators.XCANMotorControllerPIDProperties;
import xbot.common.controls.io_inputs.XCANMotorControllerInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANMotorControllerInfo;
import xbot.common.injection.electrical_contract.CANMotorControllerOutputConfig;
import xbot.common.properties.PropertyFactory;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Volts;

public class MockCANMotorController extends XCANMotorController {

    public double p;
    public double i;
    public double d;
    public double f;
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
    public void setPidDirectly(double p, double i, double d, double velocityFF, int slot) {
        this.p = p;
        this.i = i;
        this.d = d;
        this.f = velocityFF;
    }

    @Override
    public void setPower(double power) {
    }

    @Override
    public void setPowerRange(double minPower, double maxPower) {
        this.minPower = minPower;
        this.maxPower = maxPower;
    }

    @Override
    public Angle getPosition() {
        return Degrees.zero();
    }

    @Override
    public void setPosition(Angle position) {
    }

    @Override
    public void setPositionTarget(Angle position) {
    }

    @Override
    public void setPositionTarget(Angle position, int slot) {
    }

    @Override
    public AngularVelocity getVelocity() {
        return RPM.zero();
    }

    @Override
    public void setVelocityTarget(AngularVelocity velocity) {
    }

    @Override
    public void setVelocityTarget(AngularVelocity velocity, int slot) {
    }

    @Override
    protected void updateInputs(XCANMotorControllerInputs inputs) {
        inputs.angle = getPosition();
        inputs.angularVelocity = getVelocity();
        // TODO - implement test hooks for these.
        inputs.voltage = Volts.of(0);
        inputs.current = Amps.of(0);
    }
}
