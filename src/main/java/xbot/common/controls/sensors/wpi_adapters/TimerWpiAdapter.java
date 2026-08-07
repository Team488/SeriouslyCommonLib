package xbot.common.controls.sensors.wpi_adapters;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.wpilib.system.Timer;
import xbot.common.controls.sensors.XSettableTimerImpl;

@Singleton
public class TimerWpiAdapter implements XSettableTimerImpl {

    @Inject
    public TimerWpiAdapter() {}

    @Override
    public double getFPGATimestamp() {
        return Timer.getMonotonicTimestamp();
    }

    @Override
    public double getMatchTime() {
        return Timer.getMatchTime();
    }

    @Override
    public void delay(double seconds) {
        Timer.delay(seconds);
    }

    @Override
    public void setTimeInSeconds(double time) {        
        // Deliberately do nothing.
    }

    @Override
    public void advanceTimeInSecondsBy(double time) {
        // Deliberately do nothing.        
    }
}