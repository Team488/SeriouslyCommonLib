package xbot.common.logic;

import java.util.function.Supplier;

import xbot.common.controls.sensors.XTimer;
import xbot.common.logic.Latch.EdgeType;

public class TimeStableValidator {

    Latch latch;
    double risingEdgeTime;
    double stableWindow;
    Supplier<Double> stableWindowProvider;

    public TimeStableValidator(double stableWindow) {
        this.stableWindow = stableWindow;
        initialize();
    }

    public TimeStableValidator(Supplier<Double> stableWindowProvider) {
        setStableWindowProvider(stableWindowProvider);
        initialize();
    }

    private void initialize() {
        latch = new Latch(false, EdgeType.RisingEdge);
        latch.addObserver((e) -> risingEdgeTime = XTimer.getFPGATimestamp());
    }

    public void setStableWindowProvider(Supplier<Double> stableWindowProvider) {
        this.stableWindowProvider = stableWindowProvider;
    }

    public void setStableWindow(double stableWindow) {
        this.stableWindow = stableWindow;
    }

    private double getStableWindow() {
        if (stableWindowProvider == null) {
            return stableWindow;
        }
        return stableWindowProvider.get();
    }

    public boolean checkStable(boolean value) {
        latch.setValue(value);
        double duration = XTimer.getFPGATimestamp() - risingEdgeTime;
        // if the value is true now, and has been for a while, it is stable.
        return (duration > getStableWindow()) && value;
    }


}