package xbot.common.controls.sensors;

import xbot.common.math.ContiguousDouble;

public interface XGyro
{
    public boolean isConnected();
    
    public ContiguousDouble getYaw();
    
    public double getRoll();
    
    public double getPitch();
    
    public boolean isBroken();
}
