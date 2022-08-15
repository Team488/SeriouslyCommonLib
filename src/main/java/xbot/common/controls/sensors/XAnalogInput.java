package xbot.common.controls.sensors;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XAnalogInput implements XBaseIO {
    
    protected int channel;

    public interface XAnalogInputFactory {
        XAnalogInput create(int channel);
    }
    
    public XAnalogInput(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.Analog, channel, this);
    }

    public abstract double getVoltage();

    public abstract double getAverageVoltage();

    public abstract void setAverageBits(int bits);

    public abstract boolean getAsDigital(double threshold);
}
