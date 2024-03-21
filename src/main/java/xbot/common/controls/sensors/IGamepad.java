package xbot.common.controls.sensors;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N2;

public interface IGamepad {

    public Vector<N2> getLeftVector();
    public Vector<N2> getRightVector();
    
    public double getLeftTrigger();
    public double getRightTrigger();

    public void setLeftInversion(boolean xInverted, boolean yInverted);
    public void setRightInversion(boolean xInverted, boolean yInverted);
}