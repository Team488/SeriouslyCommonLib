package xbot.common.controls.actuators;

import xbot.common.controls.XBaseIO;

public interface XServo extends XBaseIO {

    public void set(double value);
}
