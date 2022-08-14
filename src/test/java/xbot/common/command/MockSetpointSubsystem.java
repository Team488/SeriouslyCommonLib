package xbot.common.command;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MockSetpointSubsystem extends BaseSetpointSubsystem {

    @Inject
    public MockSetpointSubsystem() {}

    @Override
    public double getCurrentValue() {
        return 0;
    }

    @Override
    public double getTargetValue() {
        return 0;
    }

    @Override
    public void setPower(double power) {
    }

    @Override
    public boolean isCalibrated() {
        return false;
    }

    @Override
    public void setTargetValue(double value) {
    }

}
