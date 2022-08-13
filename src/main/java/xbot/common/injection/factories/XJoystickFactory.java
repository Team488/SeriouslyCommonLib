package xbot.common.injection.factories;

import xbot.common.controls.sensors.XJoystick;

public interface XJoystickFactory {
    XJoystick create(int port, int numButtons);
}
