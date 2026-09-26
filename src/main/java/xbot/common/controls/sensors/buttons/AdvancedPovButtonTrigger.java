package xbot.common.controls.sensors.buttons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wpilib.driverstation.POVDirection;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XJoystick;

public class AdvancedPovButtonTrigger extends AdvancedTrigger {

    private static final Logger log = LogManager.getLogger(AdvancedPovButtonTrigger.class);
    
    XJoystick joystick;
    POVDirection direction;
    
    @AssistedFactory
    public abstract static class AdvancedPovButtonTriggerFactory {
        public abstract AdvancedPovButtonTrigger create(
            @Assisted("joystick") XJoystick joystick,
            @Assisted("direction") POVDirection direction);
    }

    @AssistedInject
    public AdvancedPovButtonTrigger(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("direction") POVDirection direction) {
        super(() -> joystick.getPOV() == direction);
        log.debug("Creating D-Pad button " + direction + " on port " + joystick.getPort());
        this.joystick = joystick;
        this.direction = direction;
    }

}
