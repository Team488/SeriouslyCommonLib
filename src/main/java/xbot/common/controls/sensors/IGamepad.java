package xbot.common.controls.sensors;

import xbot.common.math.XYPair;

public interface IGamepad {

    public XYPair getLeftVector();
    public XYPair getRightVector();
    
    public double getLeftTrigger();
    public double getRightTrigger();

    public void setLeftInversion(boolean xInverted, boolean yInverted);
    public void setRightInversion(boolean xInverted, boolean yInverted);
}