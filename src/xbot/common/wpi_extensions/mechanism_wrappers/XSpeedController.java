package xbot.common.wpi_extensions.mechanism_wrappers;

import edu.wpi.first.wpilibj.SpeedController;

public interface XSpeedController extends XBaseIO
{
    public double get();
    public void set(double value);
    public void disable();
    public SpeedController getInternalController();
    
    public boolean getInverted();
    public void setInverted(boolean inverted);
}
