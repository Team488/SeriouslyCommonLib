package xbot.common.controls.sensors;

import com.google.inject.Singleton;

@Singleton
public abstract class XTimer
{
    public abstract double getFPGATimestamp();
    public abstract double getMatchTime();
    public abstract void delay(double seconds);
}