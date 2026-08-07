package xbot.common.controls.actuators.wpi_adapters;

import org.wpilib.hardware.discrete.PWM;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XPWM;
import xbot.common.injection.DevicePolice;

public class PWMWPIAdapter extends XPWM
{
    // WPILib 2027 removed PWM's speed/position convenience methods along with Servo support.
    // These reimplement their historic default-bounds formula (1000-2000us range, 1500us center).
    private static final int MIN_PWM_US = 1000;
    private static final int MAX_PWM_US = 2000;
    private static final int CENTER_PWM_US = 1500;

    private final PWM pwm;

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
        pwm.setPulseTimeMicroseconds(value);
    }

    @Override
    public int getRaw() {
        return pwm.getPulseTimeMicroseconds();
    }

    @Override
    public void setSigned(double value) {
        value = Math.clamp(value, -1.0, 1.0);
        pwm.setPulseTimeMicroseconds(
                (int) Math.round(CENTER_PWM_US + value * (MAX_PWM_US - MIN_PWM_US) / 2.0));
    }

    @Override
    public double getSigned() {
        return (pwm.getPulseTimeMicroseconds() - CENTER_PWM_US) / ((MAX_PWM_US - MIN_PWM_US) / 2.0);
    }

    @Override
    public void setUnsigned(double value) {
        value = Math.clamp(value, 0.0, 1.0);
        pwm.setPulseTimeMicroseconds((int) Math.round(MIN_PWM_US + value * (MAX_PWM_US - MIN_PWM_US)));
    }

    @Override
    public double getUnsigned() {
        return (pwm.getPulseTimeMicroseconds() - MIN_PWM_US) / (double) (MAX_PWM_US - MIN_PWM_US);
    }
}
