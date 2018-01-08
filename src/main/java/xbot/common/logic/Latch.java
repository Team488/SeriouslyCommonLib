package xbot.common.logic;

import java.util.Observable;

/**
 * Logical switch which consumes booleans and signals when the given value changes.
 */
public class Latch extends Observable {

    private final EdgeType latchType;
    private boolean value;

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

        EdgeType edgeType = newValue ? EdgeType.RisingEdge : EdgeType.FallingEdge;
        if (latchType == edgeType || latchType == EdgeType.Both) {
            setChanged();
            notifyObservers(edgeType);
        }
        this.value = newValue;
    }
}