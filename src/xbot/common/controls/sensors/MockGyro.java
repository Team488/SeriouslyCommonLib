package xbot.common.controls.sensors;

import xbot.common.controls.MockRobotIO;
import xbot.common.controls.sensors.navx.AHRS;
import xbot.common.math.ContiguousHeading;

public class MockGyro implements XGyro {
    private boolean isBroken;
    private MockRobotIO mockIO;
    AHRS ahrs;

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

    public ContiguousHeading getYaw() {
        return new ContiguousHeading(mockIO.getGyroHeading());
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
    
    public double getVelocityOfYaw(){
        return ahrs.getRate();
    }

}
