package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.buttons.Button;

public class AdvancedJoystickButton extends Button
{
    private static final Logger log = Logger.getLogger(AdvancedJoystickButton.class);
    
    XJoystick joystick;
    int buttonNumber;
    
    public AdvancedJoystickButton(XJoystick joystick, int buttonNumber) {
        this.joystick = joystick;
        this.buttonNumber = buttonNumber;
    }

    private boolean isInverted = false;

    @Override
    public boolean get() {
        return joystick.getButton(buttonNumber) ^ isInverted;
    }
    
    public void setInverted(boolean inverted)
    {
        isInverted = inverted;
    }
}
