import com.google.inject.Inject;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XTimer;

public class BatteryManagement extends BaseSubsystem implements PeriodicDataSource {

    double lastPowerRecorded;
    double periodicData;
    XPowerDistributionPanel panel;
    XAnalogInput analogInput;

    @Inject
    public BatteryManagement(XPowerDistributionPanel panel, XAnalogInput analogInput)
    {
        this.panel = panel;
        this.analogInput = analogInput;
    }

    public void updatePeriodicData()
    {
        double power = panel.getTotalCurrent() * analogInput.getVoltage();
        periodicData = lastPowerRecorded - power;
    }

    public String getName()
    {
        return "";
    }

}