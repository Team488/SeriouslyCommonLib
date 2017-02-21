package xbot.common.controls.sensors;

import xbot.common.math.XYPair;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public abstract class XJoystick
{
    protected int port;
    private boolean xInverted = false;
    private boolean yInverted = false;
    
    @Inject
    public XJoystick(int port) {
        this.port = port;
    }
    
    public int getPort() {
        return port;
    }

    public boolean getXInversion() {
        return xInverted;
    }

    public void setXInversion(boolean inverted) {
        xInverted = inverted;
        
    }

    public boolean getYInversion() {
        return yInverted;
    }

    public void setYInversion(boolean inverted) {
        yInverted = inverted;        
    }
    
    public XYPair getVector() {
        return new XYPair(
                getX() * (getXInversion() ? -1 : 1),
                getY() * (getYInversion() ? -1 : 1));
    }
    
    protected abstract double getX();
    protected abstract double getY();
    
    protected abstract boolean getButton(int button);
    
    protected abstract double getRawAxis(int axisNumber);
}