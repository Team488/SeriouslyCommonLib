package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.PWM;
import xbot.common.controls.actuators.XPWM;
import xbot.common.injection.wpi_factories.DevicePolice;

public class PWMWPIAdapter extends XPWM
{
    private PWM pwm;
    
    @Inject
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
