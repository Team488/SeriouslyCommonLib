package xbot.common.controls.actuators.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.PWM;
import xbot.common.controls.actuators.XPWM;
import xbot.common.injection.DevicePolice;

public class PWMWPIAdapter extends XPWM
{
    private PWM pwm;
    
    @AssistedFactory
    public abstract static class PWMWPIAdapterFactory implements XPWMFactory {
        public abstract PWMWPIAdapter create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public PWMWPIAdapter(@Assisted("channel") int channel, DevicePolice police)
    {
        super(channel, police);
        pwm = new PWM(channel);
    }

    @Override
    public void setRaw(int value) {
        pwm.setRaw(value);
    }

    @Override
    public int getRaw() {
        return pwm.getRaw();
    }

    @Override
    public void setSigned(double value) {
        pwm.setSpeed(value);
    }

    @Override
    public double getSigned() {
        return pwm.getSpeed();
    }

    @Override
    public void setUnsigned(double value) {
        pwm.setPosition(value);
    }

    @Override
    public double getUnsigned() {
        return pwm.getPosition();
    }
}
