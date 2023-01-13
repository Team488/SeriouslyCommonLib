package xbot.common.controls.sensors.buttons;

import java.util.function.BooleanSupplier;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.XXboxController.XboxButton;

public class AdvancedXboxAxisTrigger extends AdvancedXboxButtonTrigger {

    public AdvancedXboxAxisTrigger(XXboxController controller, XboxButton buttonName, double threshold) {
        super(controller, buttonName, (BooleanSupplier)(() -> getValue(controller, buttonName, threshold)));
    }
    
    private static boolean getValue(XXboxController controller, XboxButton buttonName, double threshold) {
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
