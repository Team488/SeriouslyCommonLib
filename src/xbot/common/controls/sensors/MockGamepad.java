package xbot.common.controls.sensors;

import xbot.common.math.XYPair;

public class MockGamepad implements XGamepad {

    XYPair leftJoystickAxis;
    XYPair rightJoystickAxis;
    double leftTriggerAxis;
    double rightTriggerAxis;
    boolean leftBumper;
    boolean rightBumper;
    boolean AButton;
    boolean BButton;
    boolean XButton;
    boolean YButton;
    boolean backButton;
    boolean startButton;
    boolean leftStickButton;
    boolean rightStickButton;
    
    public void setLeftStick(double x, double y){
        leftJoystickAxis = new XYPair(x, y);
    }
    
    public void setRightStick(double x, double y){
        rightJoystickAxis = new XYPair(x, y);
    }
    
    public void setLeftTriggerValue(double x){
        leftTriggerAxis = x;
    }
    
    public void setRightTriggerValue(double x){
        rightTriggerAxis = x;
    }

    public void setLeftBumper(boolean pressed){
        leftBumper = pressed;
    }
    
    public void setRightBumper(boolean pressed){
        rightBumper = pressed;
    }
    
    public void setAButton(boolean pressed){
        AButton = pressed;
    }
    
    public void setBButton(boolean pressed){
        BButton = pressed;
    }
    
    public void setXButton(boolean pressed){
        XButton = pressed;
    }
    
    public void setYButton(boolean pressed){
        YButton = pressed;
    }
    
    public void setBackButton(boolean pressed){
        backButton = pressed;
    }
    
    public void setStartButton(boolean pressed){
        startButton = pressed;
    }
    
    public void setLeftStickButton(boolean pressed){
        leftStickButton = pressed;
    }
    
    public void setRightStickButton(boolean pressed){
        rightStickButton = pressed;
    }
    
    public XYPair getLeftStick(){
        return leftJoystickAxis;
    }
    
    public XYPair getRightStick(){
        return rightJoystickAxis;
    }
    
  
    public double getLeftStickX() {
        return leftJoystickAxis.x;
    }
    
  
    public double getRightStickX() {
        return rightJoystickAxis.x;
    }

  
    public double getLeftStickY() {
        return leftJoystickAxis.y;
    }

  
    public double getRightStickY() {
        return rightJoystickAxis.y;
    }

  
    public double getLeftTriggerAxis() {
        return leftTriggerAxis;
    }

  
    public double getRightTriggerAxis() {
        return rightTriggerAxis;
    }

  
    public boolean getLeftBumper() {
        return leftBumper;
    }

  
    public boolean getRightBumper() {
        return rightBumper;
    }
 
    public boolean getAButton() {
        return AButton;
    }

    public boolean getBButton() {
        return BButton;
    }

    public boolean getXButton() {
        return XButton;
    }
  
    public boolean getYButton() {
        return YButton;
    }
 
    public boolean getBackButton() {
        return backButton;
    }
 
    public boolean getStartButton() {
        return startButton;
    }

    public boolean getLeftStickButton() {
       return leftStickButton;
    }

    @Override
    public boolean getRightStickButton() {
        return rightStickButton;
    }
}
