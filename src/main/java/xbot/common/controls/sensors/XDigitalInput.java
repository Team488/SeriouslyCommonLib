package xbot.common.controls.sensors;

import com.google.inject.Inject;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public abstract class XDigitalInput implements XBaseIO {

    boolean inverted;
    
    @Inject
    public XDigitalInput(DevicePolice police, int channel) {
        police.registerDevice(DeviceType.DigitalIO, channel);
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
