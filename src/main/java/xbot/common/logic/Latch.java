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

    /**
     * Creates a new latch.
     * @param initialValue The initial value.
     * @param latchType The latch type.
     */
    public Latch(boolean initialValue, EdgeType latchType) {
        value = initialValue;
        this.latchType = latchType;
    }

    /**
     * Creates a new latch with a callback.
     * @param initialValue The initial value.
     * @param latchType The latch type.
     * @param callback The callback to trigger on changes.
     */
    public Latch(boolean initialValue, EdgeType latchType, Consumer<EdgeType> callback) {
        this(initialValue, latchType);
        setObserver(callback);
    }

    /**
     * Set the callback to trigger on changes.
     * @param callback The callback to trigger on changes.
     */
    public void setObserver(Consumer<EdgeType> callback) {
        this.callback = callback;
    }

    /**
     * Set the new value of the latch, triggering the callback if the value changes.
     * @param newValue The new value to latch.
     */
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