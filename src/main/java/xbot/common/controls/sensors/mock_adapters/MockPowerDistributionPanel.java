package xbot.common.controls.sensors.mock_adapters;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XPowerDistributionPanel;

/**
 * Mock implementation of XPowerDistributionPanel for testing purposes.
 * Allows setting mock voltage and mock current values for each channel.
 */
public class MockPowerDistributionPanel extends XPowerDistributionPanel {
    private final HashMap<Integer, Double> outputCurrents;

    private static final Logger log = LogManager.getLogger(MockPowerDistributionPanel.class);

    private double voltage = 12.0;

    @AssistedFactory
    public abstract static class MockPowerDistributionPanelFactory implements XPowerDistributionPanelFactory {
        @Override
        public abstract MockPowerDistributionPanel create();
    }

    @AssistedInject
    public MockPowerDistributionPanel() {
        log.info("Creating PDP");
        outputCurrents = new HashMap<>();
    }

    public void setCurrent(int channel, double current) {
        outputCurrents.put(channel, current);
    }

    public void setVoltage(double voltage) {
        this.voltage = voltage;
    }

    @Override
    public double getCurrent(int channel) {
        return outputCurrents.getOrDefault(channel, 0d);
    }

    @Override
    public double getVoltage() {
        return voltage;
    }

    @Override
    public double getTemperature() {
        return 0;
    }

    @Override
    public double getTotalCurrent() {
        return outputCurrents.values().stream().reduce(0d, Double::sum);
    }

    @Override
    public double getTotalPower() {
        return 0;
    }

    @Override
    public double getTotalEnergy() {
        return 0;
    }

    @Override
    public double getModule() {
        return 0;
    }
}
