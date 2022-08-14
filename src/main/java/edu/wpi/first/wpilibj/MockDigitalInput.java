package edu.wpi.first.wpilibj;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.injection.DevicePolice;
import xbot.common.simulation.ISimulatableSensor;

public class MockDigitalInput extends XDigitalInput implements ISimulatableSensor {

    protected boolean value;
    final int channel;

    @AssistedFactory
    public abstract static class MockDigitalInputFactory implements XDigitalInputFactory
    {
        public abstract MockDigitalInput create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public MockDigitalInput(@Assisted("channel") int channel, DevicePolice police) {
        super(police, channel);
        this.channel = channel;
    }

    public void setValue(boolean value) {
        this.value = value ^ getInverted();
    }

    @Override
    public void setInverted(boolean inverted) {
        super.setInverted(inverted);
        value = !value;
    }

    public boolean getRaw() {
        return value;
    }

    public int getChannel() {
        return this.channel;
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setValue(payload.getBoolean("Triggered"));
    }
}
