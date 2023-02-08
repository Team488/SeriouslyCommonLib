package edu.wpi.first.wpilibj;

import java.util.HashMap;

import org.apache.log4j.Logger;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XPowerDistributionPanel;

public class MockPowerDistributionPanel extends XPowerDistributionPanel {
    private HashMap<Integer, Double> outputCurrents;

    private static Logger log = Logger.getLogger(MockPowerDistributionPanel.class);

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

    @Override
    public double getCurrent(int channel) {
        return outputCurrents.getOrDefault(channel, 0d);
    }
}
