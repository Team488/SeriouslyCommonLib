package xbot.common.controls.sensors;


public interface XGamepad {

    //double
    //Joysticks
    double getXStickLeft();
    double getXStickRight();
    double getYStickLeft();
    double getYStickRight();
    
    //Triggers
    double getLeftTriggerAxis();
    double getRightTriggerAxis();
    
    //boolean
    //Bumpers
    boolean getLeftBumper();
    boolean getRightBumper();
    
    //Buttons
    boolean getAButton();
    boolean getBButton();
    boolean getXButton();
    boolean getYButton();
    boolean getBackButton();
    boolean getStartButton();
    
    //Sticks
    boolean getLeftStickButton();
    boolean getRightStickButton();
}
