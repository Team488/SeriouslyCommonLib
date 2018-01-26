package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.injection.wpi_factories.DevicePolice;

public class SpeedControllerWPIAdapter extends XSpeedController
{
    private SpeedController controller;
    
    @Inject
    public SpeedControllerWPIAdapter(@Assisted("channel") int channel, DevicePolice police)
    {
        super(channel, police);
        controller = new Talon(channel);
    }
    
    public double get()
    {
        return controller.get();
    }

    public void set(double value)
    {
        controller.set(value);
    }
}
