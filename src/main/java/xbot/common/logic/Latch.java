package xbot.common.logic;
import java.util.function.Consumer;

/**
 * Logical switch which consumes booleans and signals when the given value
 * changes.
 */
public class Latch {

    private final EdgeType latchType;
    private boolean value;
    private Consumer<EdgeType> callback;

    public enum EdgeType {
        RisingEdge, FallingEdge, Both
    }

    public Latch(boolean initialValue, EdgeType latchType) {
        value = initialValue;
        this.latchType = latchType;
    }

    public Latch(boolean initialValue, EdgeType latchType, Consumer<EdgeType> callback) {
        this(initialValue, latchType);
        addObserver(callback);
    }

    public void addObserver(Consumer<EdgeType> callback) {
        this.callback = callback;
    }

    public void setValue(boolean newValue) {
        if (this.value == newValue) {
            return; /* Do nothing if value stays the same */
        }        

        EdgeType edgeType = newValue ? EdgeType.RisingEdge : EdgeType.FallingEdge;
        if (latchType == edgeType || latchType == EdgeType.Both) {
           callback.accept(edgeType);
        }
        this.value = newValue;
    }
}