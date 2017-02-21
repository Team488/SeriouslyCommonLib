package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

public class AdvancedJoystickButton extends AdvancedButton
{
    private static final Logger log = Logger.getLogger(AdvancedJoystickButton.class);
    
    XJoystick joystick;
    int buttonNumber;
    
    public AdvancedJoystickButton(XJoystick joystick, int buttonNumber) {
        log.info("Creating button " + buttonNumber + " on port " + joystick.getPort());
        this.joystick = joystick;
        this.buttonNumber = buttonNumber;
    }

    @Override
    public boolean get() {
        return joystick.getButton(buttonNumber) ^ isInverted;
    }
}
