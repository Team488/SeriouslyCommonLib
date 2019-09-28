package xbot.common.controls.actuators;

import com.google.inject.Inject;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.XBaseIO;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public abstract class XDoubleSolenoid implements XBaseIO {

    protected boolean isInverted = false;
    public XSolenoid xSolenoid1;
    public XSolenoid xSolenoid2;
    
    @AssistedInject
    public XDoubleSolenoid(XSolenoid xSol1, XSolenoid xSol2, DevicePolice police, CommonLibFactory factory) {
        xSolenoid1 = xSol1;
        xSolenoid2 = xSol2;
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