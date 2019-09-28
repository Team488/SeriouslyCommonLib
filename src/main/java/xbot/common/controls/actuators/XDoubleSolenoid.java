package xbot.common.controls.actuators;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class XDoubleSolenoid {

    protected boolean isInverted = false;
    public XSolenoid xSolenoid1;
    public XSolenoid xSolenoid2;
    
    @AssistedInject
    public XDoubleSolenoid(@Assisted("forwardSolenoid") XSolenoid forwardSolenoid, @Assisted("xSol2") XSolenoid reverseSolenoid) {
        xSolenoid1 = forwardSolenoid;
        xSolenoid2 = reverseSolenoid;
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
            case OFF:
                setOff();
                break;
        }
    }
    
    public void setOff() {
        xSolenoid1.setOn(false);
        xSolenoid2.setOn(false);
    }

    public void setForward() {
        xSolenoid1.setOn(true);
        xSolenoid2.setOn(false);
    }

    public void setReverse() {
        xSolenoid1.setOn(false);
        xSolenoid2.setOn(true);
    }
}