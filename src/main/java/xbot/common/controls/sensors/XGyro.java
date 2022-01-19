package xbot.common.controls.sensors;

import xbot.common.math.WrappedRotation2d;

public abstract class XGyro
{   
    public enum ImuType {
        nav6,
        navX,
        mock
    }
    
    protected ImuType imuType;
    
    public XGyro(ImuType imuType) 
    {
        this.imuType = imuType;
    }
    
    public abstract boolean isBroken();
    
    protected ImuType getImuType() {
        return imuType;
    }    
    
    // Below are the "safe" methods that return gyro information. They pay attention
    // to the state of the gyro, and as such will ideally not cause exceptions.
    
    /**
     * In degrees
     */
    public WrappedRotation2d getHeading() {
        if (!isBroken()) {
            return WrappedRotation2d.fromDegrees(getDeviceYaw());
        }
        return WrappedRotation2d.fromDegrees(0);
    }
    
    public double getRoll() {
        if (!isBroken()) {
            return getDeviceRoll();
        }
        return 0;
    }
    
    public double getPitch() {
        if (!isBroken()) {
            return getDevicePitch();
        }
        return 0;
    }
    
    public double getYawAngularVelocity() {
        if (!isBroken()) {
            return getDeviceYawAngularVelocity();
        }
        return 0;
    }
    
    // What follows are the primitive "gets" for the gyro. These aren't protected,
    // and could cause exceptions if called while they gyro is not connected.
    
    public abstract boolean isConnected();
    
    /**
     * In degrees
     */
    protected abstract double getDeviceRoll();
    
    /**
     * In degrees
     */
    protected abstract double getDevicePitch();
    
    /**
     * In degrees
     */
    protected abstract double getDeviceYaw();
    
    /**
     * In degrees per second
     */
    protected abstract double getDeviceYawAngularVelocity();
    

    public abstract double getDeviceVelocityX();

    public abstract double getDeviceVelocityY();

    public abstract double getDeviceVelocityZ();

    public abstract double getDeviceRawAccelX();

    public abstract double getDeviceRawAccelY();

    public abstract double getDeviceRawAccelZ();
}
