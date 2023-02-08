package xbot.common.controls.sensors.buttons;

import java.util.function.BooleanSupplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.sensors.XJoystick;

public class AdvancedJoystickButtonTrigger extends AdvancedTrigger
{
    private static final Logger log = LogManager.getLogger(AdvancedJoystickButtonTrigger.class);
    
    XJoystick joystick;
    int buttonNumber;
    
    @AssistedFactory
    public abstract static class AdvancedJoystickButtonTriggerFactory {
        public abstract AdvancedJoystickButtonTrigger create(
            @Assisted("joystick") XJoystick joystick,
            @Assisted("buttonNumber") int buttonNumber);
    }

    @AssistedInject
    public AdvancedJoystickButtonTrigger(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("buttonNumber")int buttonNumber) {
        super((BooleanSupplier)(() -> joystick.getButton(buttonNumber)));
        log.debug("Creating button " + buttonNumber + " on port " + joystick.getPort());
        this.joystick = joystick;
        this.buttonNumber = buttonNumber;
    }
}
