package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XXboxController.XboxButton;

public class AdvancedXboxButton extends AdvancedButton {
    
    private static final Logger log = Logger.getLogger(AdvancedButton.class);
    
    XXboxController controller;
    XboxButton buttonName;
    
    public AdvancedXboxButton(XXboxController controller, XboxButton buttonName) {
        log.info("Creating XboxButton " + buttonName.toString());// + " on port " + controller.getInternalController().getPort());
        this.controller = controller;
        this.buttonName = buttonName;
    }

    @Override
    public boolean get() {
        return controller.getButton(this.buttonName.getValue());
    }
}