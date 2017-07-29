package xbot.common.controls.sensors;

public interface XPowerDistributionPanel
{
    public double getCurrent(int channel);
    public double getTotalCurrent();
    
    //TODO: Add more PDP methods
}
