package xbot.common.math;

import xbot.common.math.MovingAverage;

public class MovingAverageForDouble extends MovingAverage<Double> {
    public MovingAverageForDouble(int size) {
        super(new SumFunction<Double>() {
            @Override
            public Double add(Double a, Double b) {
                return a + b;
            }

            @Override
            public Double subtract(Double a, Double b) {
                return a - b;
            }

            @Override
            public Double divide(Double a, int b) {
                return a / b;
            }
        }, 0.0, size);
    }
}