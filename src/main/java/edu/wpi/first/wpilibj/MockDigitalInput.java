package edu.wpi.first.wpilibj;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.io_inputs.XDigitalInputs;
import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.simulation.ISimulatableSensor;

public class MockDigitalInput extends XDigitalInput implements ISimulatableSensor {

    protected boolean value;
    final int channel;

    @AssistedFactory
    public abstract static class MockDigitalInputFactory implements XDigitalInputFactory
    {
        public abstract MockDigitalInput create(@Assisted("info")DeviceInfo info);
    }

    @AssistedInject
    public MockDigitalInput(@Assisted("info") DeviceInfo info, DevicePolice police) {
        super(police, info);
        this.channel = info.channel;
    }

    public void setValue(boolean value) {
        this.value = value ^ getInverted();
    }

    @Override
    public void setInverted(boolean inverted) {
        super.setInverted(inverted);
        value = !value;
    }

    @Override
    public void updateInputs(XDigitalInputs inputs) {
        inputs.signal = value;
    }

    public int getChannel() {
        return this.channel;
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setValue(payload.getBoolean("Triggered"));
    }
}
