package xbot.common.controls.sensors;

public abstract class XTimer
{
    public abstract double getFPGATimestamp();
    public abstract double getMatchTime();
    public abstract void delay(double seconds);
}