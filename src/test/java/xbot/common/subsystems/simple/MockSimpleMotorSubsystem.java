package xbot.common.subsystems.simple;

import javax.inject.Inject;

import xbot.common.properties.TunableFactory;

public class MockSimpleMotorSubsystem extends SimpleMotorSubsystem {
    public double currentPower;

    @Inject
    public MockSimpleMotorSubsystem(TunableFactory tunableFactory) {
        super("mock", tunableFactory);
        currentPower = 0;
    }

    @Override
    public void setPower(double power) {
        this.currentPower = power;
    }
}
