package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XAnalogInput.XAnalogInputFactory;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

import java.util.function.DoubleFunction;

public class AnalogDistanceSensor extends XAnalogDistanceSensor {

    private static final int NUM_AVERAGE_BITS = 2;

    public XAnalogInput input;

    private DoubleProperty voltageOffset;
    private DoubleProperty distanceOffset;
    private DoubleProperty scalarMultiplier;

    private boolean isAveragingEnabled = false;

    private static final Logger log = Logger.getLogger(AnalogDistanceSensor.class);

    @AssistedFactory
    public abstract static class AnalogDistanceSensorFactory implements XAnalogDistanceSensorFactory {
        public abstract AnalogDistanceSensor create(
                @Assisted("channel") int channel,
                @Assisted("voltageMap") DoubleFunction<Double> voltageMap,
                @Assisted("prefix") String prefix);
    }

    @AssistedInject
    public AnalogDistanceSensor(
            XAnalogInputFactory analogInputFactory,
            @Assisted("channel") int channel,
            @Assisted("voltageMap") DoubleFunction<Double> voltageMap,
            @Assisted("prefix") String prefix,
            PropertyFactory propMan) {
        super(channel, voltageMap);

        log.info("Initializing...");
        this.input = analogInputFactory.create(channel);
        propMan.setPrefix(prefix);
        voltageOffset = propMan.createPersistentProperty("Distance sensor " + input.getChannel() + " voltage offset",
                0d);
        distanceOffset = propMan.createPersistentProperty("Distance sensor " + input.getChannel() + " distance offset",
                0d);
        scalarMultiplier = propMan
                .createPersistentProperty("Distance sensor " + input.getChannel() + "scalar multiplier", 1d);
    }

    @Override
    public double getDistance() {
        double voltage = isAveragingEnabled ? input.getAverageVoltage() : input.getVoltage();
        return (voltageMap.apply(voltage + voltageOffset.get()) + distanceOffset.get()) * scalarMultiplier.get();
    }

    @Override
    public void setAveraging(boolean shouldAverage) {
        isAveragingEnabled = shouldAverage;
        input.setAverageBits(shouldAverage ? NUM_AVERAGE_BITS : 0);
    }

    public void setVoltageOffset(double offset) {
        voltageOffset.set(offset);
    }

    public void setDistanceOffset(double offset) {
        distanceOffset.set(offset);
    }
}
