package edu.wpi.first.wpilibj;

import java.util.HashMap;
import java.util.Map;

import xbot.common.math.XYPair;
import xbot.common.wpi_extensions.mechanism_wrappers.XJoystick;

public class MockJoystick extends GenericHID implements XJoystick {
    double x = 0;
    double y = 0;
    private boolean xInverted = false; 
    private boolean yInverted = false;

    Map<Integer, Boolean> buttons = new HashMap<Integer, Boolean>();
    Map<Integer, Double> rawAxis = new HashMap<Integer, Double>();

    public MockJoystick() {
        for(int i = 0; i < 6; i++)
        {
            rawAxis.put(i, 0d);
        }
        
        for(int i = 0; i < 12; i++)
        {
            releaseButton(i);
        }
    }

    public void setX(double x) {
        double value = x * (xInverted ? -1 : 1);
        this.x = value;
        setRawAxis(0, value);
    }

    public void setY(double y) {
        double value = y * (yInverted ? -1 : 1);
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

    public XYPair getVector() {
        return new XYPair(this.x * (xInverted ? -1 : 1), this.y
                * (yInverted ? -1 : 1));
    }

    @Override
    public boolean getRawButton(int button) {
        return getButton(button);
    }
    
    @Override
    public boolean getButton(int button)
    {
        return buttons.getOrDefault(button, false);
    }

    public int getPOV(int arg0) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public boolean getXInversion() {
        return xInverted;
    }

    @Override
    public void setXInversion(boolean inverted) {
        xInverted = inverted;
    }

    @Override
    public boolean getYInversion() {
        return yInverted;
    }

    @Override
    public void setYInversion(boolean inverted) {
        yInverted = inverted;
    }

    @Override
    public GenericHID getInternalHID() {
        return this;
    }

    @Override
    @Deprecated
    public double getX(Hand hand) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    @Deprecated
    public double getY(Hand hand) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    @Deprecated
    public double getZ(Hand hand) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    @Deprecated
    public double getTwist() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    @Deprecated
    public double getThrottle() {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    public double getRawAxis(int which) {
        return rawAxis.get(which);
    }

    @Override
    public boolean getTrigger(Hand hand) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    @Deprecated
    public boolean getTop(Hand hand) {
        throw new RuntimeException("Not yet implemented");
    }

    @Override
    @Deprecated
    public boolean getBumper(Hand hand) {
        throw new RuntimeException("Not yet implemented");
    }
}
