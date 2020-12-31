package xbot.common.controls.sensors.mock_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XGyro;

public class MockGyro extends XGyro {
    private boolean isBroken;

    private double yaw;
    private double pitch;
    private double roll;
    private double yawAngularVelocity;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private double rawAccelX;
    private double rawAccelY;
    private double rawAccelZ;

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

    public void setYawAngularVelocity(double yawAngularVelocity) {
        this.yawAngularVelocity = yawAngularVelocity;
    }
    
    public double getDeviceYawAngularVelocity(){
        return yawAngularVelocity;
    }

    @Override
    public double getDeviceVelocityX() {
        return this.velocityX;
    }

    public void setDeviceVelocityX(double velocity) {
        this.velocityX = velocity;
    }

    @Override
    public double getDeviceVelocityY() {
        return this.velocityY;
    }

    public void setDeviceVelocityY(double velocity) {
        this.velocityY = velocity;
    }

    @Override
    public double getDeviceVelocityZ() {
        return this.velocityZ;
    }

    public void setDeviceVelocityZ(double velocity) {
        this.velocityZ = velocity;
    }

    @Override
    public double getDeviceRawAccelX() {
        return this.rawAccelX;
    }

    public void setDeviceRawAccelX(double accel) {
        this.rawAccelX = accel;
    }

    @Override
    public double getDeviceRawAccelY() {
        return this.rawAccelY;
    }

    public void setDeviceRawAccelY(double accel) {
        this.rawAccelY = accel;
    }

    @Override
    public double getDeviceRawAccelZ() {
        return this.rawAccelZ;
    }

    public void setDeviceRawAccelZ(double accel) {
        this.rawAccelZ = accel;
    }

}
