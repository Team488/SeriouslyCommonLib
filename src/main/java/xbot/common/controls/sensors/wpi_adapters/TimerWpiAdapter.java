package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Singleton;

import edu.wpi.first.wpilibj.Timer;
import xbot.common.controls.sensors.XTimer;

@Singleton
public class TimerWpiAdapter extends XTimer {

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