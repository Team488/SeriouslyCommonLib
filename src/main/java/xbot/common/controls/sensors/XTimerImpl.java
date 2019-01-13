package xbot.common.controls.sensors;

public interface XTimerImpl {
    public double getFPGATimestamp();
    public double getMatchTime();
    public void delay(double seconds);
}