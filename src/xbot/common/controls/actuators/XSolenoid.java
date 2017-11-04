package xbot.common.controls.actuators;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.XBaseIO;

public abstract class XSolenoid implements XBaseIO {
    
    protected boolean isInverted = false;
    protected final int channel;
    
    public XSolenoid(int channel) {
        this.channel = channel;
    }
    
    public void setOn(boolean on) {
        set(on ^ isInverted);
    }

    public boolean getAdjusted() {
        return get() ^ isInverted;
    }

    void setInverted(boolean isInverted) {
        this.isInverted = isInverted;
    }
    
    public int getChannel() {
        return channel;
    }
    
    public abstract LiveWindowSendable getLiveWindowSendable();
    protected abstract void set(boolean on);
    protected abstract boolean get();
}
