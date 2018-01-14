package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;

public abstract class XSpeedController implements XBaseIO
{
    protected int channel;
    protected boolean isInverted;
    
    public XSpeedController(int channel) {
        this.channel = channel;
    }
    
    public int getChannel() {
        return channel;
    }
    
    public boolean getInverted() {
        return isInverted;
    }
    public void setInverted(boolean isInverted) {
        this.isInverted = isInverted;
    }
    
    public void setPower(double power) {
        set(power * (isInverted ? -1 : 1));
    }
    
    public double getPower() {
        return get() * (isInverted ? -1 : 1);
    }
    
    protected abstract double get();
    protected abstract void set(double value);
}
