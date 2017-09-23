package xbot.common.controls.sensors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.MockRobotIO;

public class MockGyro extends XGyro {
    private boolean isBroken;
    private MockRobotIO mockIO;

    @AssistedInject
    public MockGyro(MockRobotIO mockRobotIO) {
        super(ImuType.mock);
        mockIO = mockRobotIO;
    }
    
    @AssistedInject
    public MockGyro(MockRobotIO mockRobotIO, @Assisted("isBroken") boolean isBroken) {
        super(ImuType.mock);
        mockIO = mockRobotIO;
        setIsBroken(isBroken);
    }

    public boolean isConnected() {
        return true;
    }

    public double getDeviceYaw() {
        return mockIO.getGyroHeading();
    }

    public void setIsBroken(boolean broken) {
        this.isBroken = broken;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public double getDeviceRoll() {
        return mockIO.getGyroRoll();
    }

    public double getDevicePitch() {
        return mockIO.getGyroPitch();
    }
    
    public double getDeviceYawAngularVelocity(){
        return mockIO.getGyroHeadingAngularVelocity();
    }

}
