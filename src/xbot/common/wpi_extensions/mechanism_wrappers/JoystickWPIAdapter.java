package xbot.common.wpi_extensions.mechanism_wrappers;

import xbot.common.math.XYPair;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;

public class JoystickWPIAdapter implements xbot.common.wpi_extensions.mechanism_wrappers.XJoystick
{
    private boolean xInverted = false;
    private boolean yInverted = false;
    private GenericHID internalHID;
    
    public JoystickWPIAdapter(int port)
    {
        internalHID = new Joystick(port);
    }
    
    public double getX()
    {
        return internalHID.getX() * (xInverted? -1:1);
    }

    public boolean getXInversion()
    {
        return xInverted;
    }

    public void setXInversion(boolean inverted)
    {
        xInverted = inverted;
        
    }

    public double getY()
    {
        return internalHID.getY() * (yInverted? -1:1);
    }

    public boolean getYInversion()
    {
        return yInverted;
    }

    public void setYInversion(boolean inverted)
    {
        yInverted = inverted;        
    }
    
    public XYPair getVector()
    {
        return new XYPair(this.getX(), this.getY());
    }

    public GenericHID getInternalHID()
    {
        return this.internalHID;
    }

    @Override
    public double getRawAxis(int axisNumber) {
        return this.internalHID.getRawAxis(axisNumber);
    }

    @Override
    public boolean getButton(int button) {
        return this.internalHID.getRawButton(button);
    }
    
}
