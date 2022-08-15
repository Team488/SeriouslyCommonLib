package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XServo implements XBaseIO {

    protected int channel;
    
    public interface XServoFactory {
        XServo create(int channel);
    }

    protected XServo(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.PWM, channel, this);
    }
    
    public int getChannel() {
        return channel;
    }
    
    public abstract void set(double value);
}
