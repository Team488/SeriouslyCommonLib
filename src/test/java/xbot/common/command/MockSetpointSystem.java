package xbot.common.command;

import com.google.inject.Singleton;

@Singleton
public class MockSetpointSystem extends BaseSetpointSubsystem {

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
