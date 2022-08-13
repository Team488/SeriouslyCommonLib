package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class AdvancedPovButton extends AdvancedButton {

    private static final Logger log = Logger.getLogger(AdvancedPovButton.class);
    
    XJoystick joystick;
    int povNumber;
    
    @AssistedFactory
    public abstract static class AdvancedPovButtonFactory {
        public abstract AdvancedPovButton create(
            @Assisted("joystick") XJoystick joystick,
            @Assisted("povNumber") int povNumber);
    }

    @AssistedInject
    public AdvancedPovButton(
            @Assisted("joystick") XJoystick joystick, 
            @Assisted("povNumber")int povNumber) {
        log.debug("Creating D-Pad button " + povNumber + " on port " + joystick.getPort());
        this.joystick = joystick;
        this.povNumber = povNumber;
    }
    
    @Override
    public boolean get() {
        return joystick.getPOV() == povNumber;
    }
}
