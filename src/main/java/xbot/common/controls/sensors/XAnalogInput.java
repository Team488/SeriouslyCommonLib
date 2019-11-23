package xbot.common.controls.sensors;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public abstract class XAnalogInput implements XBaseIO {
    
    protected int channel;
    
    public XAnalogInput(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.Analog, channel);
    }

    public abstract double getVoltage();

    public abstract double getAverageVoltage();

    public abstract void setAverageBits(int bits);

    public abstract boolean getAsDigital(double threshold);
}
