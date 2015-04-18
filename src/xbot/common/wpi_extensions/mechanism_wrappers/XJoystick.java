package xbot.common.wpi_extensions.mechanism_wrappers;

import xbot.common.math.XYPair;
import edu.wpi.first.wpilibj.GenericHID;

public interface XJoystick
{
    public boolean getXInversion();
    public void setXInversion(boolean inverted);
    
    public boolean getYInversion();
    public void setYInversion(boolean inverted);
    
    public GenericHID getInternalHID();
    
    public XYPair getVector();
    
    public boolean getButton(int button);
    
    public double getRawAxis(int axisNumber);
}