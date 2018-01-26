package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public abstract class XDigitalOutput implements XBaseIO {

    protected int channel;
    
    public XDigitalOutput(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.DigitalIO, channel);
    }
    
    public int getChannel() {
        return channel;
    }
    
    public abstract void set(boolean value);
}
