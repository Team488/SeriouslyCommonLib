package xbot.common.controls.sensors;

public abstract class XPowerDistributionPanel
{
    public interface XPowerDistributionPanelFactory {
        XPowerDistributionPanel create();
    }

    public abstract double getCurrent(int channel);
    public abstract double getVoltage();
    public abstract double getTemperature();
    public abstract double getTotalCurrent();
    public abstract double getTotalPower();
    public abstract double getTotalEnergy();
    public abstract double getModule();

}
