package xbot.common.controls.actuators;

import edu.wpi.first.wpilibj.SpeedController;
import xbot.common.controls.XBaseIO;

public interface XSpeedController extends XBaseIO
{
    public double get();
    public void set(double value);
    public void disable();
    public SpeedController getInternalController();
    
    public boolean getInverted();
    public void setInverted(boolean inverted);
}
