package xbot.common.command;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MockSetpointSubsystem extends BaseSetpointSubsystem<Double> {

    @Inject
    public MockSetpointSubsystem() {}

    @Override
    public Double getCurrentValue() {
        return 0.0;
    }

    @Override
    public Double getTargetValue() {
        return 0.0;
    }

    @Override
    public void setTargetValue(Double value) {

    }

    @Override
    public void setPower(Double power) {
    }

    @Override
    public boolean isCalibrated() {
        return false;
    }
}
