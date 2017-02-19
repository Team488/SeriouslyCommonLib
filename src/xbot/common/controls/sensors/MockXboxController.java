package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.XboxController;
import xbot.common.controls.sensors.XboxControllerWpiAdapter.XboxButtons;
import xbot.common.math.XYPair;

public class MockXboxController implements XXboxController {

    public MockXboxController(int port) {
    }

    XYPair leftJoystickAxis;
    XYPair rightJoystickAxis;
    double leftTriggerAxis;
    double rightTriggerAxis;
    double leftTriggerButton;
    double rightTriggerButton;
    
    boolean[] buttonValues = new boolean[10];
    
    public void setButton(XboxButtons buttonName, boolean pressed) {
        buttonValues[buttonName.getValue()-1] = pressed;
    }
    
    public boolean getButton(XboxButtons buttonName) {
        return buttonValues[buttonName.getValue()-1];
    }
    
    public void setLeftStick(double x, double y){
        leftJoystickAxis = new XYPair(x, y);
    }
    
    public void setRightStick(double x, double y){
        rightJoystickAxis = new XYPair(x, y);
    }
    
    public void setLeftTriggerPressed(double x){
        leftTriggerAxis = x;
    }
    
    public void setRightTriggerPressed(double x){
        rightTriggerAxis = x;
    }
    
    public XYPair getLeftStick(double x, double y){
        return leftJoystickAxis = new XYPair(x, y);
    }
    
    @Override
    public XYPair getLeftStick() {
        return null;
    }
    
    public XYPair getRightStick(double x, double y){
        return rightJoystickAxis = new XYPair(x, y);
    }
    
    @Override
    public XYPair getRightStick() {
        return null;
    }
    
    @Override
    public double getLeftStickX() {
        return leftJoystickAxis.x;
    }
    
    @Override
    public double getRightStickX() {
        return rightJoystickAxis.x;
    }

    @Override
    public double getLeftStickY() {
        return leftJoystickAxis.y;
    }

    @Override
    public double getRightStickY() {
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
    public AdvancedXboxButton getXboxButton(XboxButtons buttonName) {
        return new AdvancedXboxButton(this, buttonName);
    }

    @Override
    public boolean getRawXboxButton(int index) {
        return false;
    }

    @Override
    public XboxController getInternalController() {
        return null;
    }
}
