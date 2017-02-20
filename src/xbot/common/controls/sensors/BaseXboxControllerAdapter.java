package xbot.common.controls.sensors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID.HIDType;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;

public abstract class BaseXboxControllerAdapter {
    
    protected int port;
    
    @Inject
    public BaseXboxControllerAdapter(@Assisted("port") int port) {
        this.port = port;
    }

    public abstract String toString();

    public abstract void setRumble(RumbleType type, double value);

    public abstract void setOutputs(int value);

    public abstract void setOutput(int outputNumber, boolean value);

    public abstract String getName();

    public abstract HIDType getType();

    public abstract int getPOVCount();

    public abstract int getPOV(int pov);

    public abstract boolean getStartButton();

    public abstract boolean getBackButton();

    public abstract boolean getStickButton(Hand hand);

    public abstract boolean getYButton();

    public abstract boolean getXButton();

    public abstract boolean getBButton();

    public abstract boolean getAButton();

    public abstract double getTriggerAxis(Hand hand);

    public abstract boolean getRawButton(int button);

    public abstract boolean equals(Object obj);

    public abstract boolean getTop(Hand hand);

    public abstract int getPort();

    public abstract int getPOV();

    public abstract boolean getTrigger(Hand hand);

    public abstract boolean getBumper(Hand hand);

    public abstract double getRawAxis(int axis);

    public abstract double getY(Hand hand);

    public abstract double getY();

    public abstract double getX(Hand hand);

    public abstract double getX();

    public abstract int hashCode();

    public abstract boolean getStickButton();

    public abstract boolean getBumper();

    

}