package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public abstract class XServo implements XBaseIO {

    protected int channel;
    
    public XServo(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.PWM, channel);
    }
    
    public int getChannel() {
        return channel;
    }
    
    public abstract void set(double value);
}
