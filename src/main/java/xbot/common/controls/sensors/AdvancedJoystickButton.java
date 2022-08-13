package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class AdvancedJoystickButton extends AdvancedButton
{
    private static final Logger log = Logger.getLogger(AdvancedJoystickButton.class);
    
    XJoystick joystick;
    int buttonNumber;
    
    @AssistedFactory
    public abstract static class AdvancedJoystickButtonFactory {
        public abstract AdvancedJoystickButton create(
            @Assisted("joystick") XJoystick joystick,
            @Assisted("buttonNumber") int buttonNumber);
    }

    @AssistedInject
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
