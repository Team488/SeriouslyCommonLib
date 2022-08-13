package xbot.common.controls.sensors;

public abstract class XPowerDistributionPanel
{
    public interface XPowerDistributionPanelFactory {
        XPowerDistributionPanel create();
    }

    public abstract double getCurrent(int channel);
}
