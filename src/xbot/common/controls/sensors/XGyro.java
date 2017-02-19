package xbot.common.controls.sensors;

import xbot.common.math.ContiguousHeading;

public interface XGyro
{
    public boolean isConnected();
    
    public ContiguousHeading getYaw();
    
    public double getRoll();
    
    public double getPitch();
    
    public boolean isBroken();
    
    public double getVelocityOfYaw();
}
