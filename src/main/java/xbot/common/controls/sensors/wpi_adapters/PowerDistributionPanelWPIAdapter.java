package xbot.common.controls.sensors.wpi_adapters;

import edu.wpi.first.wpilibj.PowerDistribution;
import xbot.common.controls.sensors.XPowerDistributionPanel;

public class PowerDistributionPanelWPIAdapter extends XPowerDistributionPanel {
    
    private PowerDistribution pdp;
    
    public PowerDistributionPanelWPIAdapter() {
        pdp = new PowerDistribution();
    }
    
    @Override
    public double getCurrent(int channel) {
        return pdp.getCurrent(channel);
    }
}
