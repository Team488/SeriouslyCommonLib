package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.XboxController;
import xbot.common.controls.sensors.XboxControllerWpiAdapter.XboxButton;
import xbot.common.math.XYPair;

public interface XXboxController {

    //Joysticks----------------------------------------------------------------------------------------------
    double getLeftStickX();
    double getRightStickX();
    double getLeftStickY();
    double getRightStickY();
    XYPair getLeftStick();
    XYPair getRightStick();
 
    public boolean getRightStickXInversion();
    public void setRightStickXInversion(boolean inverted);
    
    public boolean getRightStickYInversion();
    public void setRightStickYInversion(boolean inverted);
    
    public boolean getLeftStickXInversion();
    public void setLeftStickXInversion(boolean inverted);
    
    public boolean getLeftStickYInversion();
    public void setLeftStickYInversion(boolean inverted);
    //Triggers-----------------------------------------------------------------------------------------------
    double getLeftTriggerAxis();
    double getRightTriggerAxis();
    
    AdvancedXboxButton getXboxButton(XboxButton buttonName);
    boolean getRawXboxButton(int index);
    XboxController getInternalController();
}
