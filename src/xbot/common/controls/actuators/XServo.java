package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;

public abstract class XServo implements XBaseIO {

    protected int channel;
    
    public XServo(int channel) {
        this.channel = channel;
    }
    
    public int getChannel() {
        return channel;
    }
    
    public abstract void set(double value);
}
