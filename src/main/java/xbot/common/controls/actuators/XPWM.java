package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XPWM implements XBaseIO
{
    protected int channel;
    
    public interface XPWMFactory {
        XPWM create(int channel);
    }

    protected XPWM(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.PWM, channel, this);
    }
    
    public int getChannel() {
        return channel;
    }

    /**
     * Sets the PWM duty cycle
     * @param value the value to set, in the range [0, 255]
     */
    public abstract void setRaw(int value);
    public abstract int getRaw();
    
    /**
     * Sets the PWM duty cycle
     * @param value the value to set, in the range [-1, 1]
     */
    public abstract void setSigned(double value);
    public abstract double getSigned();

    /**
     * Sets the PWM duty cycle
     * @param value the value to set, in the range [0, 1]
     */
    public abstract void setUnsigned(double value);
    public abstract double getUnsigned();
}
