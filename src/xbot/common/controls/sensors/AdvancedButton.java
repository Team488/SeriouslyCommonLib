package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.buttons.Button;

public abstract class AdvancedButton extends Button
{
    protected boolean isInverted = false;
    
    public AdvancedButton() {}

    public void setInverted(boolean inverted)
    {
        isInverted = inverted;
    }
    
    public boolean getInverted() {
        return isInverted;
    }
}
