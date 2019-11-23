package xbot.common.subsystems.power;

import com.google.inject.Inject;

import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.sensors.XPowerDistributionPanel;
import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.BaseDriveSubsystem;

public class BatteryManagementSubsystem extends BaseSubsystem implements PeriodicDataSource {

    private double lastPowerRecorded;
    private double totalPowerUsed;
    private double lastTime;
    private XPowerDistributionPanel panel;
    private BaseDriveSubsystem baseDrive;
    private final DoubleProperty wattsUsedProp;
    private final DoubleProperty estimatedCapacityProp;

    @Inject
    public BatteryManagementSubsystem(XPowerDistributionPanel panel, BaseDriveSubsystem baseDrive, PropertyFactory pf)
    {
        pf.setPrefix(this);
        this.panel = panel;
        lastPowerRecorded = 0;
        totalPowerUsed = 0;
        this.baseDrive = baseDrive;

        wattsUsedProp = pf.createEphemeralProperty("WattsUsed", 0);
        estimatedCapacityProp = pf.createPersistentProperty("EstimatedCapacity", defaultValue);
    }

    //update the total power and current power
    public void updatePeriodicData()
    {
        double currentTime = XTimer.getFPGATimestamp();
        double deltaTime = lastTime = currentTime;
        //equation for power is Current * voltage
        //getTotalCurrent gets the current from all the input panels
        double power = panel.getTotalCurrent() * panel.getVoltage();
        totalPowerUsed += power * deltaTime;
        lastTime = currentTime;
        wattsUsedProp.set(totalPowerUsed);
    }

    public double getTotalPowerUsed() {
        return wattsUsedProp.get();
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