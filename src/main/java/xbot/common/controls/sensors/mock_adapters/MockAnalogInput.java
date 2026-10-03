package xbot.common.controls.sensors.mock_adapters;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.injection.DevicePolice;
import xbot.common.simulation.ISimulatableSensor;

public class MockAnalogInput extends XAnalogInput implements ISimulatableSensor {
    int channel;
    double voltage;

    @AssistedFactory
    public abstract static class MockAnalogInputFactory implements XAnalogInputFactory {
        public abstract MockAnalogInput create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public MockAnalogInput(
            @Assisted("channel") int channel,
            DevicePolice police,
            DataFrameRegistry dataFrameRegistry) {
        super(channel, police, dataFrameRegistry);
        this.channel = channel;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getVoltage() {
        return voltage;
    }

    @Override
    public int getChannel() {
        return channel;
    }

    @Override
    public boolean getAsDigital(double threshold) {
        return getVoltage() >= threshold;
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setVoltage(payload.getDouble("Voltage"));
    }
}
