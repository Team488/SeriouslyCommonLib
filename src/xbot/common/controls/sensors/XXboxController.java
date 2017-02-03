package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.XboxController;
import xbot.common.controls.sensors.XboxControllerWpiAdapter.XboxButtons;
import xbot.common.math.XYPair;

public interface XXboxController {

    //Joysticks----------------------------------------------------------------------------------------------
    double getLeftStickX();
    double getRightStickX();
    double getLeftStickY();
    double getRightStickY();
    XYPair getLeftStick();
    XYPair getRightStick();
    
    //Triggers-----------------------------------------------------------------------------------------------
    double getLeftTriggerAxis();
    double getRightTriggerAxis();
    
    AdvancedXboxButton getXboxButton(XboxButtons buttonName);
    boolean getRawXboxButton(int index);
    XboxController getInternalController();
    
}
