package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;

public interface XSolenoid extends XBaseIO {
    public void set(boolean on);

    public boolean get();

    void setInverted(boolean isInverted);
}
