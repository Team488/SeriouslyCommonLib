package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.buttons.Button;

public class AdvancedJoystickButton extends AdvancedButton
{
    private static final Logger log = Logger.getLogger(AdvancedJoystickButton.class);
    
    XJoystick joystick;
    int buttonNumber;
    
    public AdvancedJoystickButton(XJoystick joystick, int buttonNumber) {
        this.joystick = joystick;
        this.buttonNumber = buttonNumber;
    }

    @Override
    public boolean get() {
        return joystick.getButton(buttonNumber) ^ isInverted;
    }
}
