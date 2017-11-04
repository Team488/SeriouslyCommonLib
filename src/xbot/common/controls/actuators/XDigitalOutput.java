package xbot.common.controls.actuators;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.XBaseIO;

public abstract class XDigitalOutput implements XBaseIO {

    protected int channel;
    
    public XDigitalOutput(int channel) {
        this.channel = channel;
    }
    
    public int getChannel() {
        return channel;
    }
    
    public abstract void set(boolean value);
    public abstract LiveWindowSendable getLiveWindowSendable();
}
