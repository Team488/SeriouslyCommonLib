package xbot.common.controls.sensors;

import xbot.common.math.XYPair;

public interface XGamepad {

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
    
    //Bumpers------------------------------------------------------------------------------------------------
    boolean getLeftBumper();
    boolean getRightBumper();
    
    //Buttons------------------------------------------------------------------------------------------------
    boolean getAButton();
    boolean getBButton();
    boolean getXButton();
    boolean getYButton();
    boolean getBackButton();
    boolean getStartButton();
    
    //Sticks-------------------------------------------------------------------------------------------------
    boolean getLeftStickButton();
    boolean getRightStickButton();
}
