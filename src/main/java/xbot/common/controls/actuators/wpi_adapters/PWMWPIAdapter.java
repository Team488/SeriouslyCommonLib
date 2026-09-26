package xbot.common.controls.actuators.wpi_adapters;

import org.wpilib.hardware.discrete.PWM;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XPWM;
import xbot.common.injection.DevicePolice;

public class PWMWPIAdapter extends XPWM
{
    private static final int MAX_PULSE_TIME_MICROSECONDS = 2000;
    private static final int MIN_POSITIVE_PULSE_TIME_MICROSECONDS = 1501;
    private static final int CENTER_PULSE_TIME_MICROSECONDS = 1500;
    private static final int MAX_NEGATIVE_PULSE_TIME_MICROSECONDS = 1499;
    private static final int MIN_PULSE_TIME_MICROSECONDS = 1000;
    private static final int POSITIVE_SCALE_FACTOR =
            MAX_PULSE_TIME_MICROSECONDS - MIN_POSITIVE_PULSE_TIME_MICROSECONDS;
    private static final int NEGATIVE_SCALE_FACTOR =
            MAX_NEGATIVE_PULSE_TIME_MICROSECONDS - MIN_PULSE_TIME_MICROSECONDS;
    private static final int POSITION_SCALE_FACTOR =
            MAX_PULSE_TIME_MICROSECONDS - MIN_PULSE_TIME_MICROSECONDS;

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
        pwm.setPulseTimeMicroseconds(signedValueToPulseTime(value));
    }

    @Override
    public double getSigned() {
        return pulseTimeToSignedValue(pwm.getPulseTimeMicroseconds());
    }

    @Override
    public void setUnsigned(double value) {
        pwm.setPulseTimeMicroseconds(unsignedValueToPulseTime(value));
    }

    @Override
    public double getUnsigned() {
        return pulseTimeToUnsignedValue(pwm.getPulseTimeMicroseconds());
    }

    static int signedValueToPulseTime(double value) {
        double boundedValue = Double.isFinite(value) ? Math.clamp(value, -1.0, 1.0) : 0.0;
        if (boundedValue == 0.0) {
            return CENTER_PULSE_TIME_MICROSECONDS;
        }
        if (boundedValue > 0.0) {
            return (int)Math.round(
                    boundedValue * POSITIVE_SCALE_FACTOR + MIN_POSITIVE_PULSE_TIME_MICROSECONDS);
        }
        return (int)Math.round(
                boundedValue * NEGATIVE_SCALE_FACTOR + MAX_NEGATIVE_PULSE_TIME_MICROSECONDS);
    }

    static double pulseTimeToSignedValue(int pulseTimeMicroseconds) {
        if (pulseTimeMicroseconds == 0) {
            return 0.0;
        }
        if (pulseTimeMicroseconds > MAX_PULSE_TIME_MICROSECONDS) {
            return 1.0;
        }
        if (pulseTimeMicroseconds < MIN_PULSE_TIME_MICROSECONDS) {
            return -1.0;
        }
        if (pulseTimeMicroseconds > MIN_POSITIVE_PULSE_TIME_MICROSECONDS) {
            return (double)(pulseTimeMicroseconds - MIN_POSITIVE_PULSE_TIME_MICROSECONDS)
                    / POSITIVE_SCALE_FACTOR;
        }
        if (pulseTimeMicroseconds < MAX_NEGATIVE_PULSE_TIME_MICROSECONDS) {
            return (double)(pulseTimeMicroseconds - MAX_NEGATIVE_PULSE_TIME_MICROSECONDS)
                    / NEGATIVE_SCALE_FACTOR;
        }
        return 0.0;
    }

    static int unsignedValueToPulseTime(double value) {
        double boundedValue = Double.isFinite(value) ? Math.clamp(value, 0.0, 1.0) : 0.0;
        return (int)(boundedValue * POSITION_SCALE_FACTOR) + MIN_PULSE_TIME_MICROSECONDS;
    }

    static double pulseTimeToUnsignedValue(int pulseTimeMicroseconds) {
        if (pulseTimeMicroseconds < MIN_PULSE_TIME_MICROSECONDS) {
            return 0.0;
        }
        if (pulseTimeMicroseconds > MAX_PULSE_TIME_MICROSECONDS) {
            return 1.0;
        }
        return (double)(pulseTimeMicroseconds - MIN_PULSE_TIME_MICROSECONDS) / POSITION_SCALE_FACTOR;
    }
}
