package xbot.common.controls.sensors;

import xbot.common.math.ContiguousHeading;

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
    public ContiguousHeading getHeading() {
        if (!isBroken()) {
            return new ContiguousHeading(getDeviceYaw());
        }
        return new ContiguousHeading(0);
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
}
