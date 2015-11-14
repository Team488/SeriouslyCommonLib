package xbot.common.logic;

import java.util.Observable;

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

        switch (latchType) {
        case Both:
            if (different) {
                saveValueAndAlertWatchers(value);
            }
            break;
        case RisingEdge:
            if (different && value) {
                saveValueAndAlertWatchers(value);
            }
            break;
        case FallingEdge:
            if (different && !value) {
                saveValueAndAlertWatchers(value);
            }
            break;
        default:
            break;
        }
    }

    private void saveValueAndAlertWatchers(boolean value) {
        this.value = value;

        EdgeType edgeType = value ? EdgeType.RisingEdge : EdgeType.FallingEdge;

        setChanged();
        notifyObservers(edgeType);
    }
}
