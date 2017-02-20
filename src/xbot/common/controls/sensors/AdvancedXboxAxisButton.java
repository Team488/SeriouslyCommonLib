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
                value = controller.getLeftTriggerAxis();
                break;
            case RightTrigger:
                value = controller.getRightTriggerAxis();
                break;
            default: 
                break;
        }
        
        return value > threshold;
    }
}
