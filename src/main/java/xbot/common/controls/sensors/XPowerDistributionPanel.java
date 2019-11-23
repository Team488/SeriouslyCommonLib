package xbot.common.controls.sensors;

public abstract class XPowerDistributionPanel
{
    public abstract double getCurrent(int channel);

    public abstract double getTotalCurrent();

    public abstract double getVoltage();
}
