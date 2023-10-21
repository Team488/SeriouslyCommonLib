package xbot.common.logic;

import java.util.function.Supplier;

import xbot.common.controls.sensors.XTimer;
import xbot.common.logic.Latch.EdgeType;

/**
 * Checks that a value is stable for a given amount of time.
 */
public class TimeStableValidator {

    Latch latch;
    double risingEdgeTime;
    double stableWindow;
    Supplier<Double> stableWindowProvider;

    /**
     * Creates a new TimeStableValidator using a constant value.
     * @param stableWindow The time window, in seconds.
     */
    public TimeStableValidator(double stableWindow) {
        setStableWindow(stableWindow);
        initialize();
    }

    /**
     * Creates a new TimeStableValidator using a window from a supplier.
     * @param stableWindowProvider A supplier that provides a time window, in seconds.
     */
    public TimeStableValidator(Supplier<Double> stableWindowProvider) {
        setStableWindowProvider(stableWindowProvider);
        initialize();
    }

    private void initialize() {
        latch = new Latch(false, EdgeType.RisingEdge);
        latch.setObserver((e) -> risingEdgeTime = XTimer.getFPGATimestamp());
    }

    /**
     * Update the stable window using a supplier.
     * @param stableWindowProvider A supplier that provides a time window, in seconds.
     */
    public void setStableWindowProvider(Supplier<Double> stableWindowProvider) {
        this.stableWindowProvider = stableWindowProvider;
    }

    /**
     * Update the stable window using a constant value.
     * @param stableWindow The time window, in seconds.
     */
    public void setStableWindow(double stableWindow) {
        this.stableWindow = stableWindow;
    }

    private double getStableWindow() {
        if (stableWindowProvider == null) {
            return stableWindow;
        }
        return stableWindowProvider.get();
    }

    /**
     * Checks if the value has been stable for the entire stable window duration.
     * @param value The value to test.
     * @return True if the value has been stable for the entire window duration.
     */
    public boolean checkStable(boolean value) {
        latch.setValue(value);
        double duration = XTimer.getFPGATimestamp() - risingEdgeTime;
        // if the value is true now, and has been for a while, it is stable.
        return (duration > getStableWindow()) && value;
    }

    /**
     * Used if you want the validator to start in a stable state.
     */
    public void setStable() {
        latch.setValue(true);
        risingEdgeTime = -10000;
    }


}