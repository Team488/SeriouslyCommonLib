package xbot.common.injection.factories;

import xbot.common.controls.sensors.XFTCGamepad;

public interface XFTCGamepadFactory {
    XFTCGamepad create(int port, int numButtons);
}
