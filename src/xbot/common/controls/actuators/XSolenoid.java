package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;

public abstract class XSolenoid implements XBaseIO {
    public void set(boolean on);

    public boolean get();

    void setInverted(boolean isInverted);
}
