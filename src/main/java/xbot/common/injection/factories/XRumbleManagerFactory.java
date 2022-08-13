package xbot.common.injection.factories;

import xbot.common.controls.sensors.XJoystick;
import xbot.common.subsystems.feedback.XRumbleManager;

public interface XRumbleManagerFactory {
    XRumbleManager create(XJoystick gamepad);
}
