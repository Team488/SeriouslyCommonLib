package xbot.common.controls.sensors.mock_adapters;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.ISimulatableSensor;

public class MockEncoder extends XEncoder implements ISimulatableSensor {

    private double distance;
    private double rate;

    @AssistedFactory
    public abstract static class MockEncoderFactory implements XEncoderFactory {
        public abstract MockEncoder create(
            @Assisted("name") String name,
            @Assisted("aChannel") int aChannel,
            @Assisted("bChannel") int bChannel,
            @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse);
    }

    @AssistedInject
    public MockEncoder(@Assisted("name") String name, @Assisted("aChannel") int aChannel,
            @Assisted("bChannel") int bChannel, @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse,
            PropertyFactory propMan, DevicePolice police) {
        super(name, aChannel, bChannel, defaultDistancePerPulse, propMan, police);
    }

    public MockEncoder(String prefix, PropertyFactory propMan) {
        super(prefix, propMan);
    }

    public void setDistance(double distance) {
        this.distance = distance * (isInverted ? -1 : 1);
    }

    protected double getRate() {
        return rate;
    }

    public void setRate(double newRate) {
        this.rate = newRate;
    }

    protected double getDistance() {
        return distance;
    }

    public void setSamplesToAverage(int samples) {
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setDistance((double)payload.get("EncoderTicks"));
    }
}
