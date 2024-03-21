package xbot.common.controls.sensors;

import edu.wpi.first.math.geometry.Translation2d;

public interface IGamepad {

    public Translation2d getLeftVector();
    public Translation2d getRightVector();
    
    public double getLeftTrigger();
    public double getRightTrigger();

    public void setLeftInversion(boolean xInverted, boolean yInverted);
    public void setRightInversion(boolean xInverted, boolean yInverted);
}