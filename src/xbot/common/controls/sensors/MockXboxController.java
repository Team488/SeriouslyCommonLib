package xbot.common.controls.sensors;

import java.util.HashMap;
import java.util.Map;

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
    private boolean xRightInverted = false;
    private boolean yRightInverted = false;
    private boolean xLeftInverted = false;
    private boolean yLeftInverted = false;
    
    Map<Integer, Boolean> buttons = new HashMap<Integer, Boolean>();
    
    public void setRightX(double x) {
        rightJoystickAxis.x = x;
    }

    public void setLeftX(double x) {
        leftJoystickAxis.x = x;
    }
    
    public void setRightY(double y) {
        rightJoystickAxis.y = y;
    }

    public void setLeftY(double y) {
        leftJoystickAxis.y = y;
    }

    public void pressButton(int button) {
        buttons.put(button, true);
    }

    public void releaseButton(int button) {
        buttons.put(button, false);
    }

    public XYPair getRightVector() {
        return new XYPair(this.rightJoystickAxis.x * (xRightInverted ? -1 : 1), this.rightJoystickAxis.y
                * (yRightInverted ? -1 : 1));
    }

    public XYPair getLeftVector() {
        return new XYPair(this.leftJoystickAxis.x * (xLeftInverted ? -1 : 1), this.leftJoystickAxis.y
                * (yLeftInverted ? -1 : 1));
    }
    
    public boolean getRawButton(int button) {
        return getButton(button);
    }
    
    public boolean getButton(int button)
    {
        return buttons.getOrDefault(button, false);
    }
    
    public void setButton(XboxButtons buttonName, boolean pressed) {
        buttons.put(buttonName.getValue(), pressed);
    }
    
    public boolean getButton(XboxButtons buttonName) {
        return getButton(buttonName.getValue());
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

    @Override
    public void setRightStickXInversion(boolean inverted) {
        xRightInverted = inverted;
    }

    @Override
    public void setRightStickYInversion(boolean inverted) {
        yRightInverted = inverted;
    }

    @Override
    public void setLeftStickXInversion(boolean inverted) {
        xLeftInverted = inverted;
    }

    @Override
    public void setLeftStickYInversion(boolean inverted) {
        yLeftInverted = inverted;
    }
    
    @Override
    public boolean getRightStickXInversion() {
        return xRightInverted;
    }
    
    @Override
    public boolean getRightStickYInversion() {
        return yRightInverted;
    }

    @Override
    public boolean getLeftStickXInversion() {
        return xLeftInverted;
    }
    
    @Override
    public boolean getLeftStickYInversion() {
        return yLeftInverted;
    }
}