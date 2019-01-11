package xbot.common.controls.sensors;

public class XTimer
{
    private static XTimerImpl impl;
    
    public static void setImplementation(XTimerImpl implementation) {
        impl = implementation;
    }

    public static double getFPGATimestamp() {
        return impl.getFPGATimestamp();
    }

    public double getMatchTime() {
        return impl.getMatchTime();
    }
    public void delay(double seconds) {
        impl.delay(seconds);
    }

}