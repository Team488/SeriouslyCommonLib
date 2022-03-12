package xbot.common.controls.sensors;

public class XTimer
{
    private static XTimerImpl impl;
    
    public static void setImplementation(XTimerImpl implementation) {
        impl = implementation;
    }

    /**
     * Returns the current time in seconds since the FPGA was powered on.
     * @return Time in seconds
     */
    public static double getFPGATimestamp() {
        return impl.getFPGATimestamp();
    }

    public static double getMatchTime() {
        return impl.getMatchTime();
    }
    public static void delay(double seconds) {
        impl.delay(seconds);
    }

}