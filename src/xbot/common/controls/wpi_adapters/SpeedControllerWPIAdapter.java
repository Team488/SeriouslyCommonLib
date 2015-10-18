package xbot.common.controls.wpi_adapters;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import xbot.common.controls.XSpeedController;

public class SpeedControllerWPIAdapter implements XSpeedController
{
    private SpeedController controller;
    private boolean inverted = false;
    final int channel;
    
    public SpeedControllerWPIAdapter(int channel)
    {
        controller = new Talon(channel);
        this.channel = channel;
    }
    
    public double get()
    {
        return controller.get() * (inverted? -1d:1d);
    }

    public void set(double value)
    {
        controller.set(value * (inverted? -1d:1d));
    }

    public void disable()
    {
        controller.disable();
    }

    public SpeedController getInternalController()
    {
        return controller;
    }

    public boolean getInverted()
    {
        return inverted;
    }

    public void setInverted(boolean inverted)
    {
        this.inverted = inverted;
    }

    @Override
    public int getChannel() {
        return this.channel;
    }

}
