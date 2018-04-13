package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class AdvancedPovButton extends AdvancedButton {

    private static final Logger log = Logger.getLogger(AdvancedPovButton.class);
    
    XJoystick joystick;
    int povNumber;
    
    @Inject
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
