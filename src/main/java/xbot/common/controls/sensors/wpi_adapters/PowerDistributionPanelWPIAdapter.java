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
}
