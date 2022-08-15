package xbot.common.controls.sensors;

import java.math.BigDecimal;
import java.util.function.DoubleFunction;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.ISimulatableSensor;

public class SimulatedAnalogDistanceSensor extends XAnalogDistanceSensor implements ISimulatableSensor {

    private double distance;

    @AssistedFactory
    public abstract static class SimulatedAnalogDistanceSensorFactory implements XAnalogDistanceSensorFactory {
        public abstract SimulatedAnalogDistanceSensor create(
                @Assisted("channel") int channel,
                @Assisted("voltageMap") DoubleFunction<Double> voltageMap,
                @Assisted("prefix") String prefix);
    }

    @AssistedInject
    public SimulatedAnalogDistanceSensor(@Assisted("channel") int channel,
            @Assisted("voltageMap") DoubleFunction<Double> voltageMap, @Assisted("prefix") String prefix,
            PropertyFactory propMan, DevicePolice police) {
        super(channel, voltageMap);

        police.registerDevice(DeviceType.Analog, channel, this);
    }

    @Override
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public void setAveraging(boolean shouldAverage) {
    }

    @Override
    public void setVoltageOffset(double offset) {
    }

    @Override
    public void setDistanceOffset(double offset) {
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        // Some sort of BigDecimal issue
        BigDecimal intermediate = (BigDecimal) payload.get("Distance");
        setDistance(intermediate.doubleValue());
    }

}