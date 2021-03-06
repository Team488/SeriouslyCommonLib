package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public abstract class XSolenoid implements XBaseIO {
    
    protected boolean isInverted = false;
    protected final int channel;

    public XSolenoid(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.Solenoid, this.channel, 0, getMaxSupportedChannel());
    }
    
    public void setOn(boolean on) {
        set(on ^ isInverted);
    }

    public boolean getAdjusted() {
        return get() ^ isInverted;
    }

    public void setInverted(boolean isInverted) {
        this.isInverted = isInverted;
    }
    
    public int getChannel() {
        return channel;
    }
    
    protected abstract void set(boolean on);
    protected abstract boolean get();
    protected abstract int getMaxSupportedChannel();
}
