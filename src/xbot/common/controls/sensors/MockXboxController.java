package xbot.common.controls.sensors;


import java.util.HashMap;
import java.util.Map;

import com.google.inject.Inject;


import edu.wpi.first.wpilibj.XboxController;
import xbot.common.controls.sensors.XboxControllerWpiAdapter.XboxButton;
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
        setButton(button, true);
    }

    public void releaseButton(int button) {
        setButton(button, false);
    }

    public XYPair getRightVector() {
        return new XYPair(
                this.rightJoystickAxis.x * (getRightStickXInversion() ? -1 : 1), 
                this.rightJoystickAxis.y * (getRightStickYInversion() ? -1 : 1));
    }

    public XYPair getLeftVector() {
        return new XYPair(
                this.leftJoystickAxis.x * (getLeftStickXInversion() ? -1 : 1), 
                this.leftJoystickAxis.y * (getLeftStickYInversion() ? -1 : 1));
    }
    
    public boolean getRawButton(int button) {
        return getButton(button);
    }
    
    public void setButton(int button, boolean pressed) {
        buttons.put(button, pressed);
    }
    
    public void setButton(XboxButton buttonName, boolean pressed) {
        setButton(buttonName.getValue(), pressed);
    }
    
    public boolean getButton(XboxButton buttonName) {
        return getButton(buttonName.getValue());
    }
    
    public boolean getButton(int button)
    {
        return buttons.getOrDefault(button, false);
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
    
    @Override
    public double getLeftStickX() {
        return getLeftVector().x;
    }
    
    @Override
    public double getRightStickX() {
        return getRightVector().x;
    }

    @Override
    public double getLeftStickY() {
        return getLeftVector().y;
    }

    @Override
    public double getRightStickY() {
        return getRightVector().y;
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
    public AdvancedXboxButton getXboxButton(XboxButton buttonName) {
        if (buttonName == XboxButton.LeftTrigger || buttonName == XboxButton.RightTrigger) {
            return new AdvancedXboxAxisButton(this, buttonName, 0.75);
        }
        return new AdvancedXboxButton(this, buttonName);
    }

    @Override
    public boolean getRawXboxButton(int index) {
        return getButton(index);
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

    @Override
    public XYPair getLeftStick() {
        return getLeftVector();
    }

    @Override
    public XYPair getRightStick() {
        return getRightVector();
    }
}