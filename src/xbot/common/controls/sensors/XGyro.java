package xbot.common.controls.sensors;

import xbot.common.math.ContiguousHeading;

public interface XGyro
{
    public boolean isConnected();
    
    /**
     * In degrees
     */
    public ContiguousHeading getYaw();
    
    /**
     * In degrees
     */
    public double getRoll();
    
    /**
     * In degrees
     */
    public double getPitch();
    
    public boolean isBroken();
    
    /**
     * In degrees per second
     */
    public double getYawAngularVelocity();
}
