package xbot.common.math;

import java.util.LinkedList;
import java.util.Queue;

public class MovingAverage<T> {
    private int size = 15;
    private Queue<T> queue;
    private T sum;
    private SumFunction<T> sumFunction;

    public MovingAverage(SumFunction<T> sumFunction, T initialValue, int size) {
        this.queue = new LinkedList<>();
        this.sum = initialValue;
        this.sumFunction = sumFunction;
        this.size = size;
    }

    public void add(T value) {
        sum = sumFunction.add(sum, value);
        queue.add(value);
        if (queue.size() > size) {
            sum = sumFunction.subtract(sum, queue.remove());
        }
    }

    public T getAverage() {
        if (queue.isEmpty()) {
            return sum;
        }
        return sumFunction.divide(sum, queue.size());
    }

    public interface SumFunction<T> {
        T add(T a, T b);
        T subtract(T a, T b);
        T divide(T a, int b);
    }

}

