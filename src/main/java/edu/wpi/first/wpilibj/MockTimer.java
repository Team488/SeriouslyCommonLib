package edu.wpi.first.wpilibj;

import com.google.inject.Singleton;

import xbot.common.controls.sensors.XTimer;

@Singleton
public class MockTimer extends XTimer {

    double timeInSeconds;

    public void setTimeInSeconds(double time) {
        timeInSeconds = time;
    }

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