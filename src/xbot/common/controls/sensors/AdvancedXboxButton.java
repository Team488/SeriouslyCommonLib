package xbot.common.controls.sensors;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XboxControllerWpiAdapter.XboxButtons;

public class AdvancedXboxButton extends AdvancedButton {
    
    private static final Logger log = Logger.getLogger(AdvancedButton.class);
    
    XXboxController controller;
    XboxButtons buttonName;
    
    public AdvancedXboxButton(XXboxController controller, XboxButtons buttonName) {
        this.controller = controller;
        this.buttonName = buttonName;
    }

    @Override
    public boolean get() {
        return controller.getRawXboxButton(this.buttonName.getValue());
    }
}