package xbot.common.controls.sensors;

import xbot.common.controls.MockRobotIO;
import xbot.common.math.ContiguousHeading;

public class MockGyro extends XGyro {
    private boolean isBroken;
    private MockRobotIO mockIO;

    public MockGyro(MockRobotIO mockRobotIO) {
        super(ImuType.mock);
        mockIO = mockRobotIO;
    }
    
    public MockGyro(MockRobotIO mockRobotIO, boolean isBroken) {
        super(ImuType.mock);
        mockIO = mockRobotIO;
        setIsBroken(isBroken);
    }

    public boolean isConnected() {
        return true;
    }

    public double getYaw() {
        return mockIO.getGyroHeading();
    }

    public void setIsBroken(boolean broken) {
        this.isBroken = broken;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public double getRoll() {
        return mockIO.getGyroRoll();
    }

    public double getPitch() {
        return mockIO.getGyroPitch();
    }
    
    public double getYawAngularVelocity(){
        return mockIO.getGyroHeadingAngularVelocity();
    }

}
