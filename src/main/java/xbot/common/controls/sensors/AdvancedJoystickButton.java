package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class AdvancedJoystickButton extends AdvancedButton
{
    private static final Logger log = Logger.getLogger(AdvancedJoystickButton.class);
    
    XJoystick joystick;
    int buttonNumber;
    
    @Inject
    public AdvancedJoystickButton(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("buttonNumber")int buttonNumber) {
        log.debug("Creating button " + buttonNumber + " on port " + joystick.getPort());
        this.joystick = joystick;
        this.buttonNumber = buttonNumber;
    }

    @Override
    public boolean get() {
        return joystick.getButton(buttonNumber) ^ isInverted;
    }
}
