package xbot.common.controls.sensors;

import xbot.common.controls.sensors.XXboxController.XboxButton;

public class AdvancedXboxDPadButton extends AdvancedXboxButton {
    
    public AdvancedXboxDPadButton(XXboxController controller, XboxButton buttonName) {
        super(controller, buttonName);
    }
    
    @Override 
    public boolean get(){
        int angle = controller.getPOV();
        return angle == getAngleForDPadButton(buttonName);
    }
    
    private static int getAngleForDPadButton(XboxButton buttonName) {
        switch (buttonName) {
        case DPadUp:
            return 0;
        case DPadRight:
            return 90;
        case DPadDown:
            return 180;
        case DPadLeft:
            return 270;
        default:
            return -1;
        }
    }
}
