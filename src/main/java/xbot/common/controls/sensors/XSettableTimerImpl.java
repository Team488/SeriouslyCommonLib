package xbot.common.controls.sensors;

public interface XSettableTimerImpl extends XTimerImpl {

    /**
     * Set the current time
     * @param time The current time in seconds
     */
    public void setTimeInSeconds(double time);

    /**
     * Advance the timer
     * @param time Duration in seconds to advance by
     */
    public void advanceTimeInSecondsBy(double time);

}