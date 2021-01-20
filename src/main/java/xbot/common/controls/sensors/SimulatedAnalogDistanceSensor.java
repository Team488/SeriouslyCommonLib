package xbot.common.controls.sensors;

import java.util.function.DoubleFunction;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.json.JSONObject;

import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.ISimulatableSensor;

public class SimulatedAnalogDistanceSensor extends XAnalogDistanceSensor implements ISimulatableSensor {

    private double distance;

    @Inject
    public SimulatedAnalogDistanceSensor(CommonLibFactory clf, @Assisted("channel") int channel,
            @Assisted("voltageMap") DoubleFunction<Double> voltageMap, PropertyFactory propMan, DevicePolice police) {
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
        setDistance((double)payload.get("Distance"));
    }

}