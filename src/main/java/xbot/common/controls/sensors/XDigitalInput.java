package xbot.common.controls.sensors;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XDigitalInput implements XBaseIO {

    boolean inverted;
    
    public interface XDigitalInputFactory {
        XDigitalInput create(int channel);
    }

    public XDigitalInput(DevicePolice police, int channel) {
        police.registerDevice(DeviceType.DigitalIO, channel, this);
    }
    
    public boolean get() {
        return getRaw() ^ inverted;
    }
    
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
    
    public boolean getInverted() {
        return inverted;
    }
    
    protected abstract boolean getRaw();
}
