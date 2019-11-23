package xbot.common.subsystems.feedback;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.sensors.XFTCGamepad;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XTimer;

/**
 * Wrappers around gamepad rumble behavior to control intensity and duration.
 */
@Singleton
public class RumbleManager implements PeriodicDataSource {
    private double lastRequestEndTime = -1;
    private XJoystick gamepad;
    private boolean isRumbling;
    
    /**
     * Creates a new RumbleManager instance.
     * @param gamepad The gamepad in which to control rumble on.
     */
    @Inject
    public RumbleManager(XFTCGamepad gamepad) {
        this.gamepad = gamepad;
    }
    
    /**
     * Immediately stops rumbling on the gamepad.
     */
    public void stopGamepadRumble() {
        writeRumble(gamepad, 0);
        lastRequestEndTime = -1;
    }
    
    /**
     * Rumbles the gamepad at the specified intensity for the given duration.
     * @param intensity Rumble intensity, between 0 and 1.
     * @param length Rumble duration, in seconds
     */
    public void rumbleGamepad(double intensity, double length) {
        writeRumble(gamepad, intensity);
        lastRequestEndTime = XTimer.getFPGATimestamp() + length;
    }
    
    /**
     * Writes the rumble value to the specified joystick.
     * @param joystick Joystick to set the rumble value on.
     * @param intensity Rumble intensity, between 0 and 1.
     */
    private void writeRumble(XJoystick joystick, double intensity) {

        GenericHID internalJoystick = joystick.getRawWPILibJoystick();
        isRumbling = intensity > 0;
        if (internalJoystick == null) {
            return;
        }
        internalJoystick.setRumble(RumbleType.kLeftRumble, intensity);
        internalJoystick.setRumble(RumbleType.kRightRumble, intensity);
    }

    /**
     * Gets the current rumble state.
     * @return true if the gamepad is currently rumbling.
     */
    public boolean getIsRumbling() {
        return isRumbling;
    }

    /**
     * Called periodically to update the rumble state.
     */
    @Override
    public void updatePeriodicData() {
        if (isRumbling && lastRequestEndTime > 0 && XTimer.getFPGATimestamp() > lastRequestEndTime) {
            writeRumble(gamepad, 0);
            lastRequestEndTime = -1;
            isRumbling = false;
        }
    }

    /**
     * Gets the name of the data source.
     */
    @Override
    public String getName() {
        return "RumbleManager";
    }
}
