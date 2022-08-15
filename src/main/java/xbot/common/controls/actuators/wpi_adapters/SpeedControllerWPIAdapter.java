package xbot.common.controls.actuators.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.motorcontrol.PWMMotorController;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.injection.DevicePolice;

public class SpeedControllerWPIAdapter extends XSpeedController
{
    private PWMMotorController controller;
    
    @AssistedFactory
    public abstract static class SpeedControllerWPIAdapterFactory implements XSpeedControllerFactory {
        public abstract SpeedControllerWPIAdapter create(@Assisted("channel") int channel);
    }

    @AssistedInject
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
