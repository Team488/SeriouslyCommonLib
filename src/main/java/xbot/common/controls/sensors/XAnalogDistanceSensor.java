package xbot.common.controls.sensors;

import java.util.function.DoubleUnaryOperator;

public abstract class XAnalogDistanceSensor implements DistanceSensor {

    DoubleUnaryOperator voltageMap;

    public interface XAnalogDistanceSensorFactory {
        XAnalogDistanceSensor create(
            int channel,
            DoubleUnaryOperator voltageMap,
            String prefix);
    }

    protected XAnalogDistanceSensor(
            int channel,
            DoubleUnaryOperator voltageMap) {
                this.voltageMap = voltageMap;
    }

    public abstract double getDistance();

    public abstract void setAveraging(boolean shouldAverage);

    public abstract void setVoltageOffset(double offset);

    public abstract void setDistanceOffset(double offset);

    public static class VoltageMaps
    {
        public static final double sharp0A51SK(double voltage)
        {
            // 3.6601x4 - 20.375x3 + 41.593x2 - 38.528x + 15.848
            return (3.6601 * Math.pow(voltage, 4d))
                    - (20.375 * Math.pow(voltage, 3d))
                    + (41.593 * Math.pow(voltage, 2d))
                    - (38.528 * voltage)
                    + 15.848;
        }
    }
}