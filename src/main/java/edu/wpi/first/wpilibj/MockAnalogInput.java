package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.json.JSONObject;

import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.simulation.ISimulatableSensor;

public class MockAnalogInput extends XAnalogInput implements ISimulatableSensor {
    int channel;
    double voltage;

    @Inject
    public MockAnalogInput(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.channel = channel;
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    public double getVoltage() {
        return voltage;
    }

    public double getAverageVoltage() {
        return voltage;
    }

    public void setAverageBits(int bits) {
        // Nothing to do here.
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
