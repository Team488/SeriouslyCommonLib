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
    
    protected ImuType getImuType() {
        return imuType;
    }
    
    public abstract boolean isConnected();
    
    /**
     * In degrees
     */
    public ContiguousHeading getHeading() {
        return new ContiguousHeading(getYaw());
    }
    
    /**
     * In degrees
     */
    public abstract double getRoll();
    
    /**
     * In degrees
     */
    public abstract double getPitch();
    
    /**
     * In degrees
     */
    protected abstract double getYaw();
    
    public abstract boolean isBroken();
    
    /**
     * In degrees per second
     */
    public abstract double getYawAngularVelocity();
    
    
}
