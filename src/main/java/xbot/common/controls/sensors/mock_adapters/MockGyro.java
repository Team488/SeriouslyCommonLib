package xbot.common.controls.sensors.mock_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XGyro;

public class MockGyro extends XGyro {
    private boolean isBroken;

    private double yaw;
    private double pitch;
    private double roll;
    private double angularVelocity;

    @AssistedInject
    public MockGyro() {
        super(ImuType.mock);
    }
    
    @AssistedInject
    public MockGyro(@Assisted("isBroken") boolean isBroken) {
        super(ImuType.mock);
        setIsBroken(isBroken);
    }

    public boolean isConnected() {
        return true;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getDeviceYaw() {
        return yaw;
    }

    public void setIsBroken(boolean broken) {
        this.isBroken = broken;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getDeviceRoll() {
        return roll;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getDevicePitch() {
        return pitch;
    }

    public void setYawAngularVelocity(double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    public double getDeviceYawAngularVelocity(){
        return angularVelocity;
    }

}
