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
    double x = 0;
    double y = 0;
    double rightTriggerAxis;
    private boolean xRightInverted = false;
    private boolean yRightInverted = false;
    private boolean xLeftInverted = false;
    private boolean yLeftInverted = false;
    
    Map<Integer, Boolean> buttons = new HashMap<Integer, Boolean>();
    Map<Integer, Double> rawAxis = new HashMap<Integer, Double>();
    
    boolean[] buttonValues = new boolean[10];
    
    public void setRightX(double x) {
        double value = x * (xRightInverted ? -1 : 1);
        this.x = value;
        setRawAxis(0, value);
    }

    public void setLeftX(double x) {
        double value = x * (xLeftInverted ? -1 : 1);
        this.x = value;
        setRawAxis(0, value);
    }
    
    public void setRightY(double y) {
        double value = y * (yRightInverted ? -1 : 1);
        this.y = value;
        setRawAxis(1, value);
    }

    public void setLeftY(double y) {
        double value = y * (yLeftInverted ? -1 : 1);
        this.y = value;
        setRawAxis(1, value);
    }
    
    public void setRawAxis(int which, double value) {
        rawAxis.put(which, value);
    }

    public void pressButton(int button) {
        buttons.put(button, true);
    }

    public void releaseButton(int button) {
        buttons.put(button, false);
    }

    public XYPair getRightVector() {
        return new XYPair(this.x * (xRightInverted ? -1 : 1), this.y
                * (yRightInverted ? -1 : 1));
    }

    public XYPair getLeftVector() {
        return new XYPair(this.x * (xLeftInverted ? -1 : 1), this.y
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
        // TODO Auto-generated method stub
        return null;
    }
    
    public XYPair getRightStick(double x, double y){
        return rightJoystickAxis = new XYPair(x, y);
    }
    
    @Override
    public XYPair getRightStick() {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return new AdvancedXboxButton(this, buttonName);
    }

    @Override
    public boolean getRawXboxButton(int index) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public XboxController getInternalController() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRightStickXInversion(boolean inverted) {
        // TODO Auto-generated method stub
        xRightInverted = inverted;
    }

    @Override
    public void setRightStickYInversion(boolean inverted) {
        // TODO Auto-generated method stub
        yRightInverted = inverted;
    }

    @Override
    public void setLeftStickXInversion(boolean inverted) {
        // TODO Auto-generated method stub
        xLeftInverted = inverted;
    }

    @Override
    public void setLeftStickYInversion(boolean inverted) {
        // TODO Auto-generated method stub
        yLeftInverted = inverted;
    }
    
    @Override
    public boolean getRightStickXInversion() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean getRightStickYInversion() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getLeftStickXInversion() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean getLeftStickYInversion() {
        // TODO Auto-generated method stub
        return false;
    }
