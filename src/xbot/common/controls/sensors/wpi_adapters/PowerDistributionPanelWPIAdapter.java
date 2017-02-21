package xbot.common.controls.sensors.wpi_adapters;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.sensors.XPowerDistributionPanel;

public class PowerDistributionPanelWPIAdapter extends XPowerDistributionPanel {
    
    private PowerDistributionPanel pdp;
    
    public PowerDistributionPanelWPIAdapter() {
        pdp = new PowerDistributionPanel();
    }
    
    @Override
    public double getCurrent(int channel) {
        return pdp.getCurrent(channel);
    }

    @Override
    public LiveWindowSendable getLiveWindowSendable() {
        return this.pdp;
    }

}
