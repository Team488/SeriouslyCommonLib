import com.google.inject.Inject;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.sensors.XAnalogInput;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XTimer;
import xbot.common.subsystems.drive.BaseDriveSubsystem;

public class BatteryManagement extends BaseSubsystem implements PeriodicDataSource {

    double lastPowerRecorded;
    double periodicData;
    double totalPowerUsed;
    XPowerDistributionPanel panel;
    XAnalogInput analogInput;
    BaseDriveSubsystem baseDrive;

    @Inject
    public BatteryManagement(XPowerDistributionPanel panel, XAnalogInput analogInput, BaseDriveSubsystem baseDrive)
    {
        this.panel = panel;
        this.analogInput = analogInput;
        lastPowerRecorded = 0;
        totalPowerUsed = 0;
        this.baseDrive = baseDrive;
    }

    //update the total power and current power
    public void updatePeriodicData()
    {
        //equation for power is Current * voltage
        //getTotalCurrent gets the current from all the input panels
        double power = panel.getTotalCurrent() * analogInput.getVoltage();
        periodicData = power;
        totalPowerUsed += power;
        //periodicData = Abs(lastPowerRecorded - power);
    }

/*
    public void antiChattering(double powerLimit, int secondsFromNeutralToFullWhenChattering, int regularSecondsFromNeutralToFull)
    {
        if (totalPowerUsed > powerLimit)
        {
            baseDrive.setVoltageRamp(secondsFromNeutralToFullWhenChattering);
            //setCurrentLimits()
            //configVoltageCompSaturation
        }
        else
        {
            baseDrive.setVoltageRamp(regularSecondsFromNeutralToFull);
        }
        //setVoltageRamp() --> BaseDriveSubsystem.java
        //setCurrentLimits()
    }

    /*
    public void antiChattering(double batteryVoltageLimit)
    {
        if (analogInput.getVoltage() > batteryVoltageLimit)
        {
            baseDrive.setVoltageRamp(secondsFromNeutralToFullWhenChattering);
        }
    }
    */

}