package xbot.common.controls.sensors.buttons;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.sensors.XJoystick;

public class AdvancedPovButtonTrigger extends AdvancedTrigger {

    private static final Logger log = Logger.getLogger(AdvancedPovButtonTrigger.class);
    
    XJoystick joystick;
    int povNumber;
    
    @AssistedFactory
    public abstract static class AdvancedPovButtonTriggerFactory {
        public abstract AdvancedPovButtonTrigger create(
            @Assisted("joystick") XJoystick joystick,
            @Assisted("povNumber") int povNumber);
    }

    @AssistedInject
    public AdvancedPovButtonTrigger(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("povNumber")int povNumber) {
        super(() -> joystick.getPOV() == povNumber);
        log.debug("Creating D-Pad button " + povNumber + " on port " + joystick.getPort());
        this.joystick = joystick;
        this.povNumber = povNumber;
    }

}
