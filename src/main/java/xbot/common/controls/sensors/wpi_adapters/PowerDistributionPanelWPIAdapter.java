package xbot.common.controls.sensors.wpi_adapters;

import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import org.littletonrobotics.conduit.ConduitApi;
import xbot.common.controls.sensors.XPowerDistributionPanel;

public class PowerDistributionPanelWPIAdapter extends XPowerDistributionPanel {

    private final ConduitApi conduit;

    @AssistedFactory
    public abstract static class PowerDistributionPanelWPIAdapaterFactory implements XPowerDistributionPanelFactory {
        @Override
        public abstract PowerDistributionPanelWPIAdapter create();
    }

    @AssistedInject
    public PowerDistributionPanelWPIAdapter() {
        // We need to use ConduitApi to get PDP settings to avoid conflicts with AdvantageKit
        conduit = ConduitApi.getInstance();
    }

    @Override
    public double getCurrent(int channel) {
        return conduit.getPDPChannelCurrent(channel);
    }

    @Override
    public double getVoltage() {
        return conduit.getPDPVoltage();
    }

    @Override
    public double getTemperature() {
        return conduit.getPDPTemperature();
    }

    @Override
    public double getTotalCurrent() {
        return conduit.getPDPTotalCurrent();
    }

    @Override
    public double getTotalPower() {
        return conduit.getPDPTotalPower();
    }

    @Override
    public double getTotalEnergy() {
        return conduit.getPDPTotalEnergy();
    }

    @Override
    public double getModule() {
        return conduit.getPDPModuleId();
    }
}
