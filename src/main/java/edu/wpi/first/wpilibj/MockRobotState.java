package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.RobotState;

public class MockRobotState implements RobotState.Interface {

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isOperatorControl() {
        return false;
    }

    @Override
    public boolean isAutonomous() {
        return false;
    }

    @Override
    public boolean isTest() {
        return false;
    }

}