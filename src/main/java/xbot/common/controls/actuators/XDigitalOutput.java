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
    
    /**
     * Set the PWM frequency of ALL DIGITAL OUTPUT PWM CHANNELS
     * @param frequency
     */
    public abstract void setPWMRate(double frequency);
    public abstract void enablePWM(double initialDutyCycle);
    public abstract void updateDutyCycle(double dutyCycle);
    public abstract void disablePWM();
}
