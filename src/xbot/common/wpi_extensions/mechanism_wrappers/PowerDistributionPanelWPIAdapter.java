package xbot.common.wpi_extensions.mechanism_wrappers;

import edu.wpi.first.wpilibj.PowerDistributionPanel;

public class PowerDistributionPanelWPIAdapter implements XPowerDistributionPanel {
    private PowerDistributionPanel pdp;
    
    public PowerDistributionPanelWPIAdapter() {
        pdp = new PowerDistributionPanel();
    }
    
    @Override
    public double getCurrent(int channel) {
        return pdp.getCurrent(channel);
    }

}
