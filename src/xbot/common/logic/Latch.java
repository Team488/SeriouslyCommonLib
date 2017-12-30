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

    public void setValue(boolean newValue) {
        if (this.value == newValue) {
            return; /* Do nothing if value stays the same */
        }

        this.value = newValue;
        EdgeType edgeType = newValue ? EdgeType.RisingEdge : EdgeType.FallingEdge;
        boolean alertWatchers;
        switch (latchType) {
            case RisingEdge:
                alertWatchers = newValue; /* Alert Watchers if newValue equals true */
                break;
            case FallingEdge:
                alertWatchers = !newValue; /* Alert Watchers if newValue equals false */
                break;
            case Both:
                alertWatchers = true;
                break;
            default:
                alertWatchers = false;
                break;
        }

        if (alertWatchers) {
            setChanged();
            notifyObservers(edgeType);
        }
    }
}