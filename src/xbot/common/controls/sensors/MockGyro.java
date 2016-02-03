package xbot.common.controls.sensors;

import xbot.common.controls.MockRobotIO;
import xbot.common.math.ContiguousDouble;

public class MockGyro implements XGyro {
    private boolean isBroken;
    private MockRobotIO mockIO;

    public MockGyro(MockRobotIO mockRobotIO) {
        mockIO = mockRobotIO;
    }
    
    public MockGyro(MockRobotIO mockRobotIO, boolean isBroken) {
        mockIO = mockRobotIO;
        setIsBroken(isBroken);
    }

    public boolean isConnected() {
        return true;
    }

    public ContiguousDouble getYaw() {
        return new ContiguousDouble(mockIO.getGyroHeading(), -180, 180);
    }

    public void setIsBroken(boolean broken) {
        this.isBroken = broken;
    }

    public boolean isBroken() {
        return isBroken;
    }

    @Override
    public double getRoll() {
        return mockIO.getGyroRoll();
    }

    @Override
    public double getPitch() {
        return mockIO.getGyroPitch();
    }

}
