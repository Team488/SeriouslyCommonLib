package xbot.common.logic;

import java.util.Observable;

/**
 * Logical switch which consumes booleans and signals when the given value changes.
 */
public class Latch extends Observable {

    private boolean value;
    private EdgeType latchType;

    public enum EdgeType {
        RisingEdge, FallingEdge, Both
    }

    public Latch(boolean initialValue, EdgeType latchType) {
        value = initialValue;
        this.latchType = latchType;
    }

    public void setValue(boolean value) {
        boolean different = this.value != value;

        if (different) {
            switch (latchType) {
                case Both:
                    saveValueAndAlertWatchers(value);
                    break;
                case RisingEdge:
                    if (value)
                        saveValueAndAlertWatchers(true);
                    break;
                case FallingEdge:
                    if (!value) {
                        saveValueAndAlertWatchers(false);
                    }
                    break;
                default:
                    this.value = value;
                    break;
            }
        }
    }

    private void saveValueAndAlertWatchers(boolean value) {
        EdgeType edgeType = value ? EdgeType.RisingEdge : EdgeType.FallingEdge;

        setChanged();
        notifyObservers(edgeType);
    }
}