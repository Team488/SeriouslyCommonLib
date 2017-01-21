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
    
    public void setLeftTrigger(double x){
        leftTriggerAxis = x;
    }
    
    public void setRightTrigger(double x){
        rightTriggerAxis = x;
    }
    
    /**
     * Sets the bumper state. True means pressed/on. False means not pressed/off.
     * @param b
     */
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
    
    @Override
    public double getXStickLeft() {
        return leftJoystickAxis.x;
    }
    
    @Override
    public double getXStickRight() {
        return rightJoystickAxis.x;
    }

    @Override
    public double getYStickLeft() {
        return leftJoystickAxis.y;
    }

    @Override
    public double getYStickRight() {
        return rightJoystickAxis.y;
    }

    @Override
    public double getLeftTriggerAxis() {
        return leftTriggerAxis;
    }

    @Override
    public double getRightTriggerAxis() {
        return rightTriggerAxis;
    }

    @Override
    public boolean getLeftBumper() {
        return leftBumper;
    }

    @Override
    public boolean getRightBumper() {
        return rightBumper;
    }

    @Override
    public boolean getAButton() {
        return AButton;
    }

    @Override
    public boolean getBButton() {
        return BButton;
    }

    @Override
    public boolean getXButton() {
        return XButton;
    }

    @Override
    public boolean getYButton() {
        return YButton;
    }

    @Override
    public boolean getBackButton() {
        return backButton;
    }

    @Override
    public boolean getStartButton() {
        return startButton;
    }

    @Override
    public boolean getLeftStickButton() {
       return leftStickButton;
    }

    @Override
    public boolean getRightStickButton() {
        return rightStickButton;
    }



}
