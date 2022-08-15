package xbot.common.subsystems.feedback;

import xbot.common.controls.sensors.XJoystick;

public interface XRumbleManager {

    public interface XRumbleManagerFactory {
        XRumbleManager create(XJoystick gamepad);
    }

    /**
     * Rumbles the gamepad at the specified intensity for the given duration.
     * @param intensity Rumble intensity, between 0 and 1.
     * @param length Rumble duration, in seconds
     */
    void rumbleGamepad(double intensity, double length);

    /**
     * Immediately stops rumbling on the gamepad.
     */
    void stopGamepadRumble();

    /**
     * Gets the current rumble state.
     * @return true if the gamepad is currently rumbling.
     */
    boolean getIsRumbling();

    /**
     * Called periodically to update the rumble state.
     */
    void periodic();
}
