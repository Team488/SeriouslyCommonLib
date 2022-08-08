package edu.wpi.first.wpilibj;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.controls.sensors.XSettableTimerImpl;

@Singleton
public class MockTimer implements XSettableTimerImpl {

    @Inject
    public MockTimer() {}

    double timeInSeconds;

    @Override
    public void setTimeInSeconds(double time) {
        timeInSeconds = time;
    }

    @Override
    public void advanceTimeInSecondsBy(double time) {
        timeInSeconds += time;
    }

    /**
     * Return the system clock time in seconds. Return the time from the FPGA hardware clock in seconds since the FPGA
     * started.
     *
     * @return Robot running time in seconds.
     */
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

    }
}