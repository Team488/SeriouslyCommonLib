package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Singleton;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.controls.sensors.XTimerImpl;

@Singleton
public class TimerWpiAdapter implements XTimerImpl {

    @Override
    public double getFPGATimestamp() {
        return Timer.getFPGATimestamp();
    }

    @Override
    public double getMatchTime() {
        return Timer.getMatchTime();
    }

    @Override
    public void delay(double seconds) {
        Timer.delay(seconds);
    }
}