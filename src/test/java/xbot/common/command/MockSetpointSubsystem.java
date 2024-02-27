package xbot.common.command;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MockSetpointSubsystem extends BaseSetpointSubsystem<Double> {

    private Double currentValue = 0.0;

    @Inject
    public MockSetpointSubsystem() {}

    @Override
    public Double getCurrentValue() {
        return this.currentValue;
    }

    public void setCurrentValue(double value) {
        this.currentValue = value;
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

    @Override
    protected boolean areTwoTargetsEquivalent(Double target1, Double target2) {
        return BaseSetpointSubsystem.areTwoDoublesEquivalent(target1, target2);
    }
}
