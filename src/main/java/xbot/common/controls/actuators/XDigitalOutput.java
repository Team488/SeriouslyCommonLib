package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XDigitalOutput implements XBaseIO {

    protected int channel;
    
    public interface XDigitalOutputFactory {
        XDigitalOutput create(int channel);
    }

    protected XDigitalOutput(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.DigitalIO, channel, this);
    }
    
    public int getChannel() {
        return channel;
    }
    
    public abstract void set(boolean value);
    
    /**
     * Set the PWM frequency of ALL DIGITAL OUTPUT PWM CHANNELS
     * @param frequency PWM frequency
     */
    public abstract void setPWMRate(double frequency);
    public abstract void enablePWM(double initialDutyCycle);
    public abstract void updateDutyCycle(double dutyCycle);
    public abstract void disablePWM();
    public abstract boolean get();
}
