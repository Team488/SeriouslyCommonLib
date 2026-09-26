package xbot.common.controls.sensors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import org.wpilib.tunable.TunableDouble;
import xbot.common.controls.sensors.XAnalogInput.XAnalogInputFactory;
import xbot.common.properties.TunableFactory;

import java.util.function.DoubleUnaryOperator;

public class AnalogDistanceSensor extends XAnalogDistanceSensor {

    private static final int AVERAGE_SAMPLE_WINDOW = 4;

    public XAnalogInput input;

    private TunableDouble voltageOffset;
    private TunableDouble distanceOffset;
    private TunableDouble scalarMultiplier;

    private boolean isAveragingEnabled = false;

    private static final Logger log = LogManager.getLogger(AnalogDistanceSensor.class);

    @AssistedFactory
    public abstract static class AnalogDistanceSensorFactory implements XAnalogDistanceSensorFactory {
        public abstract AnalogDistanceSensor create(
                @Assisted("channel") int channel,
                @Assisted("voltageMap") DoubleUnaryOperator voltageMap,
                @Assisted("prefix") String prefix);
    }

    @AssistedInject
    public AnalogDistanceSensor(
            XAnalogInputFactory analogInputFactory,
            @Assisted("channel") int channel,
            @Assisted("voltageMap") DoubleUnaryOperator voltageMap,
            @Assisted("prefix") String prefix,
            TunableFactory tunableFactory) {
        super(channel, voltageMap);

        log.info("Initializing...");
        this.input = analogInputFactory.create(channel);
        tunableFactory.setPrefix(prefix);
        voltageOffset = tunableFactory.createDouble("Distance sensor " + input.getChannel() + " voltage offset",
                0d);
        distanceOffset = tunableFactory.createDouble("Distance sensor " + input.getChannel() + " distance offset",
                0d);
        scalarMultiplier = tunableFactory
                .createDouble("Distance sensor " + input.getChannel() + "scalar multiplier", 1d);
    }

    @Override
    public double getDistance() {
        double voltage = isAveragingEnabled ? input.getAverageVoltage() : input.getVoltage();
        return (voltageMap.applyAsDouble(voltage + voltageOffset.get()) + distanceOffset.get()) * scalarMultiplier.get();
    }

    @Override
    public void setAveraging(boolean shouldAverage) {
        isAveragingEnabled = shouldAverage;
        input.setAverageSampleWindow(shouldAverage ? AVERAGE_SAMPLE_WINDOW : 1);
    }

    public void setVoltageOffset(double offset) {
        voltageOffset.set(offset);
    }

    public void setDistanceOffset(double offset) {
        distanceOffset.set(offset);
    }
}
