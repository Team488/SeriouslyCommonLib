package xbot.common.wpi_extensions.mechanism_wrappers;

import xbot.common.math.*;

public interface XGyro
{
    public boolean isConnected();
    
    public ContiguousDouble getYaw();
    
    public double getRoll();
    
    public double getPitch();
    
    public boolean isBroken();
}
