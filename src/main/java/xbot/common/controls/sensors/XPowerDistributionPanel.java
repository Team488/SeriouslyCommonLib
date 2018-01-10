package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;

public abstract class XPowerDistributionPanel
{
    public abstract double getCurrent(int channel);
}
