package xbot.common.controls.sensors;

public abstract class XPowerDistributionPanel
{
    public interface XPowerDistributionPanelFactory {
        XPowerDistributionPanel create();
    }

    public abstract double getCurrent(int channel);
    public abstract double getVoltage(int channel);
    public abstract double getTemperature(int channel);
    public abstract double getTotalCurrent(int channel);
    public abstract double getTotalPower(int channel);
    public abstract double getTotalEnergy(int channel);
    public abstract double getModule(int channel);

}
