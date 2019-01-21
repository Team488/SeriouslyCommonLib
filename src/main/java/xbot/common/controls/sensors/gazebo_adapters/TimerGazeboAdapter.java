package xbot.common.controls.sensors.gazebo_adapters;

import com.google.inject.Singleton;

import xbot.common.controls.sensors.XTimerImpl;

@Singleton
public class TimerGazeboAdapter implements XTimerImpl {

    @Override
    public double getFPGATimestamp() {
        return 0;
    }

    @Override
    public double getMatchTime() {
        return 0;
    }

    @Override
    public void delay(double seconds) {

    }

}