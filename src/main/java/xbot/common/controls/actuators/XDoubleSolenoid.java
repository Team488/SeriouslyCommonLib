package xbot.common.controls.actuators;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class XDoubleSolenoid {

    protected boolean isInverted = false;
    public XSolenoid forwardSolenoid;
    public XSolenoid reverseSolenoid;
    
    @AssistedInject
    public XDoubleSolenoid(@Assisted("forwardSolenoid") XSolenoid forwardSolenoid, @Assisted("reverseSolenoid") XSolenoid reverseSolenoid) {
        this.forwardSolenoid = forwardSolenoid;
        this.reverseSolenoid = reverseSolenoid;
    }

    public void setInverted(boolean isInverted) {
        this.isInverted = isInverted;
    }

    public enum DoubleSolenoidMode {
        OFF,
        FORWARD,
        REVERSE
    }

    public void setDoubleSolenoid(DoubleSolenoidMode mode) {
        if (mode == DoubleSolenoidMode.FORWARD && isInverted)
        {
            mode = DoubleSolenoidMode.REVERSE;
        }
        else if (mode == DoubleSolenoidMode.REVERSE && isInverted)
        {
            mode = DoubleSolenoidMode.FORWARD;
        }

        switch (mode) {

            case FORWARD: 
                setForward();
                break;
            case REVERSE:
                setReverse();
                break;
            default:
                setOff();
                break;
        }
    }
    
    public void setOff() {
        forwardSolenoid.setOn(false);
        reverseSolenoid.setOn(false);
    }

    public void setForward() {
        forwardSolenoid.setOn(true);
        reverseSolenoid.setOn(false);
    }

    public void setReverse() {
        forwardSolenoid.setOn(false);
        reverseSolenoid.setOn(true);
    }
}