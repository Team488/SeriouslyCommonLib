package xbot.common.controls.sensors.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XEncoderInputs;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.properties.TunableFactory;

public class MockEncoder extends XEncoder {

    private double distance;
    private double rate;

    @AssistedFactory
    public abstract static class MockEncoderFactory implements XEncoderFactory {
        public abstract MockEncoder create(@Assisted("name") String name, @Assisted("aChannel") int aChannel, @Assisted("bChannel") int bChannel,
                @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse, @Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public MockEncoder(@Assisted("name") String name, @Assisted("aChannel") int aChannel, @Assisted("bChannel") int bChannel,
            @Assisted("defaultDistancePerPulse") double defaultDistancePerPulse, @Assisted("owningSystemPrefix") String owningSystemPrefix,
            TunableFactory tunableFactory, DevicePolice police, DataFrameRegistry dataFrameRegistry) {
        super(name, aChannel, bChannel, defaultDistancePerPulse, owningSystemPrefix, tunableFactory, police, dataFrameRegistry);
    }

    public void setDistance(double distance) {
        this.distance = distance * (isInverted ? -1 : 1);
    }

    public void addDistance(double distance) {
        this.setDistance(((isInverted ? -1 : 1) * this.getDistance()) + distance);
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

    @Override
    protected void setRateWindowMilliseconds(int rateWindowMilliseconds) {
    }

    @Override
    public void updateInputs(XEncoderInputs inputs) {
        inputs.rate = getRate();
        inputs.distance = getDistance();
    }
}
