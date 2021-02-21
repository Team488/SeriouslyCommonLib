package xbot.common.controls.sensors.mock_adapters;

import com.google.inject.Singleton;

import xbot.common.controls.sensors.XSettableTimerImpl;

/**
 * A settable timer for use in simulations
 */
@Singleton
public class MockSettableTimer implements XSettableTimerImpl {

    double timeInSeconds;

    @Override
    public void setTimeInSeconds(double time) {
        timeInSeconds = time;
    }

    @Override
    public void advanceTimeInSecondsBy(double time) {
        timeInSeconds += time;
    }

    @Override
    public double getFPGATimestamp() {
        return timeInSeconds;
    }

    @Override
    public double getMatchTime() {
        return timeInSeconds;
    }

    @Override
    public void delay(double seconds) {
        try {
            Thread.sleep((long) (seconds * 1e3));
        } catch (final InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

}