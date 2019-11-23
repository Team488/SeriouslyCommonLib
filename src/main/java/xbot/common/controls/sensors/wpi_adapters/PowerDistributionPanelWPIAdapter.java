package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Singleton;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import xbot.common.controls.sensors.XPowerDistributionPanel;

@Singleton
public class PowerDistributionPanelWPIAdapter extends XPowerDistributionPanel {

    private PowerDistributionPanel pdp;

    public PowerDistributionPanelWPIAdapter() {
        pdp = new PowerDistributionPanel();
    }

    @Override
    public double getCurrent(int channel) {
        return pdp.getCurrent(channel);
    }

    public double getTotalCurrent() {
       return pdp.getTotalCurrent();
    }

    public double getVoltage() {
        return pdp.getVoltage();
    }
}
