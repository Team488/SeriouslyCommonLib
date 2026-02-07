package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.PowerDistribution;
import xbot.common.controls.sensors.XPowerDistributionPanel;

public class PowerDistributionPanelWPIAdapter extends XPowerDistributionPanel {

    private PowerDistribution pdp;

    @AssistedFactory
    public abstract static class PowerDistributionPanelWPIAdapaterFactory implements XPowerDistributionPanelFactory {
        @Override
        public abstract PowerDistributionPanelWPIAdapter create();
    }

    @AssistedInject
    public PowerDistributionPanelWPIAdapter() {
        pdp = new PowerDistribution();
    }

    @Override
    public double getCurrent(int channel) {
        return pdp.getCurrent(channel);
    }

    @Override
    public double getVoltage() {
        return pdp.getVoltage();
    }

    @Override
    public double getTemperature() {
        return pdp.getTemperature();
    }

    @Override
    public double getTotalCurrent() {
        return pdp.getTotalCurrent();
    }

    @Override
    public double getTotalPower() {
        return pdp.getTotalPower();
    }

    @Override
    public double getTotalEnergy() {
        return pdp.getTotalEnergy();
    }

    @Override
    public double getModule() {
        return pdp.getModule();
    }
}
