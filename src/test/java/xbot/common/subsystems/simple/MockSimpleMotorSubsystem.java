package xbot.common.subsystems.simple;

import xbot.common.properties.PropertyFactory;

import javax.inject.Inject;

public class MockSimpleMotorSubsystem extends SimpleMotorSubsystem {
    public double currentPower;

    @Inject
    public MockSimpleMotorSubsystem(PropertyFactory pf) {
        super("mock", pf);
        currentPower = 0;
    }

    @Override
    public void setPower(double power) {
        this.currentPower = power;
    }
}
