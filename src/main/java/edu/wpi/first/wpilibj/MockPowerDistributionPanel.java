package edu.wpi.first.wpilibj;

import java.util.HashMap;

import com.google.inject.Singleton;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XPowerDistributionPanel;

@Singleton
public class MockPowerDistributionPanel extends XPowerDistributionPanel {
    private HashMap<Integer, Double> outputCurrents;
    private double inputVoltage;

    private static Logger log = Logger.getLogger(MockPowerDistributionPanel.class);

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

    public double getTotalCurrent()
    {
        return outputCurrents.values().stream().mapToDouble(d->d).sum();
    }

    public void setVoltage(double value) {
        inputVoltage = value;
    }

    @Override
    public double getVoltage() {
        return inputVoltage;
    }
}
