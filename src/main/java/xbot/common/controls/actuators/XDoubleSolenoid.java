package xbot.common.controls.actuators;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

public class XDoubleSolenoid {

    protected boolean isInverted = false;
    public XSolenoid forwardSolenoid;
    public XSolenoid reverseSolenoid;

    @AssistedFactory
    public abstract static class XDoubleSolenoidFactory {
        public abstract XDoubleSolenoid create(
            @Assisted("forwardSolenoid") XSolenoid forwardSolenoid,
            @Assisted("reverseSolenoid") XSolenoid reverseSolenoid);
    }
    
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
                setForwardInternal();
                break;
            case REVERSE:
                setReverseInternal();
                break;
            default:
                setOffInternal();
                break;
        }
    }

    public DoubleSolenoidMode getDoubleSolenoidMode() {
        DoubleSolenoidMode modeCandidate = DoubleSolenoidMode.OFF;
        
        if(forwardSolenoid.getAdjusted()) {
            modeCandidate = DoubleSolenoidMode.FORWARD;
        } else if(reverseSolenoid.getAdjusted()) {
            modeCandidate = DoubleSolenoidMode.REVERSE;
        }

        if (isInverted && modeCandidate == DoubleSolenoidMode.FORWARD) {
            modeCandidate = DoubleSolenoidMode.REVERSE;
        } else if (isInverted && modeCandidate == DoubleSolenoidMode.REVERSE) {
            modeCandidate = DoubleSolenoidMode.FORWARD;
        }

        return modeCandidate;
    }

    public boolean getIsForward() {
        return getDoubleSolenoidMode() == DoubleSolenoidMode.FORWARD;
    }

    public boolean getIsReverse() {
        return getDoubleSolenoidMode() == DoubleSolenoidMode.REVERSE;
    }

    public boolean getIsOff() {
        return getDoubleSolenoidMode() == DoubleSolenoidMode.OFF;
    }
    
    public void setOff() {
        setDoubleSolenoid(DoubleSolenoidMode.OFF);
    }

    public void setForward() {
        setDoubleSolenoid(DoubleSolenoidMode.FORWARD);
    }

    public void setReverse() {
        setDoubleSolenoid(DoubleSolenoidMode.REVERSE);
    }

    private void setOffInternal() {
        forwardSolenoid.setOn(false);
        reverseSolenoid.setOn(false);
    }

    private void setForwardInternal() {
        forwardSolenoid.setOn(true);
        reverseSolenoid.setOn(false);
    }

    private void setReverseInternal() {
        forwardSolenoid.setOn(false);
        reverseSolenoid.setOn(true);
    }
}