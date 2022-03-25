package xbot.common.controls.sensors;

import xbot.common.controls.sensors.XXboxController.XboxButton;

public class AdvancedXboxAxisButton extends AdvancedXboxButton {

    private double threshold;
    
    public AdvancedXboxAxisButton(XXboxController controller, XboxButton buttonName, double threshold) {
        super(controller, buttonName);
        
        this.threshold = threshold;
    }
    
    @Override 
    public boolean get(){
        double value = 0;
        
        switch(buttonName) {
            case LeftTrigger:
                value = controller.getLeftTrigger();
                break;
            case RightTrigger:
                value = controller.getRightTrigger();
                break;
                case LeftJoystickYAxis:
                value = controller.getLeftStickY();
                break;
                case RightJoystickYAxis:
                value = controller.getRightStickY();
                break;
            default: 
                break;
        }
        
        return Math.abs(value) > threshold;
    }
}
