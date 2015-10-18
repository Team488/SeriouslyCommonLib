package xbot.common.controls;

import xbot.common.math.ContiguousDouble;

public interface XGyro
{
    public boolean isConnected();
    
    public ContiguousDouble getYaw();
    
    public double getRoll();
    
    public double getPitch();
    
    public boolean isBroken();
}
