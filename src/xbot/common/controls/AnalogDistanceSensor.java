package xbot.common.controls;

import org.apache.log4j.Logger;

import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyManager;

import java.util.function.DoubleFunction;

public class AnalogDistanceSensor implements DistanceSensor {
    
    private static final int NUM_AVERAGE_BITS = 2;

    XAnalogInput inputPort;
    DoubleFunction<Double> voltageMap;
    
    private DoubleProperty voltageOffset;
    private DoubleProperty distanceOffset;
    
    private boolean isAveragingEnabled = false;
    
    private static final Logger log = Logger.getLogger(AnalogDistanceSensor.class);

    public AnalogDistanceSensor(XAnalogInput inputPort, DoubleFunction<Double> voltageMap, PropertyManager propMan) {
        log.info("Initializing...");
        this.inputPort = inputPort;
        this.voltageMap = voltageMap;
        voltageOffset = propMan.createProperty("Distance sensor " + inputPort.getChannel() + " voltage offset", 0d);
        distanceOffset = propMan.createProperty("Distance sensor " + inputPort.getChannel() + " distance offset", 0d);
    }

    @Override
    public double getDistance() {
        double voltage = isAveragingEnabled ? inputPort.getAverageVoltage() : inputPort.getVoltage();
        return voltageMap.apply(voltage + voltageOffset.get()) + distanceOffset.get();
    }

    @Override
    public void setAveraging(boolean shouldAverage) {
        isAveragingEnabled = shouldAverage;
        inputPort.setAverageBits(shouldAverage? NUM_AVERAGE_BITS : 0);
    }

    public void setVoltageOffset(double offset)
    {
        voltageOffset.set(offset);
    }
    
    public void setDistanceOffset(double offset)
    {
        distanceOffset.set(offset);
    }
    
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
