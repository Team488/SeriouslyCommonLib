package xbot.common.controls.actuators.wpi_adapters;

import org.wpilib.drivers.motor.Talon;
import org.wpilib.hardware.motor.PWMMotorController;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

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
        return controller.getThrottle();
    }

    public void set(double value)
    {
        controller.setThrottle(value);
    }
}
