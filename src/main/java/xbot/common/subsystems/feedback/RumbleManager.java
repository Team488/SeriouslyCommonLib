package xbot.common.subsystems.feedback;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import xbot.common.controls.sensors.XJoystick;
import xbot.common.controls.sensors.XTimer;

/**
 * Wrappers around gamepad rumble behavior to control intensity and duration.
 */
public class RumbleManager implements XRumbleManager {
    private double lastRequestEndTime = -1;
    private XJoystick gamepad;
    private boolean isRumbling;
    
    @AssistedFactory
    public abstract static class RumbleManagerFactory implements XRumbleManagerFactory {
        public abstract RumbleManager create(@Assisted("gamepad") XJoystick gamepad);
    }

    /**
     * Creates a new RumbleManager instance.
     * @param gamepad The gamepad in which to control rumble on.
     */
    @AssistedInject
    public RumbleManager(@Assisted("gamepad") XJoystick gamepad) {
        this.gamepad = gamepad;
    }
    
    public void stopGamepadRumble() {
        writeRumble(gamepad, 0);
        lastRequestEndTime = -1;
    }
    
    public void rumbleGamepad(double intensity, double length) {
        writeRumble(gamepad, intensity);
        lastRequestEndTime = XTimer.getFPGATimestamp() + length;
    }

    public boolean getIsRumbling() {
        return isRumbling;
    }

    public void periodic() {
        if (isRumbling && lastRequestEndTime > 0 && XTimer.getFPGATimestamp() > lastRequestEndTime) {
            writeRumble(gamepad, 0);
            lastRequestEndTime = -1;
            isRumbling = false;
        }
    }
    
    /**
     * Writes the rumble value to the specified joystick.
     * @param joystick Joystick to set the rumble value on.
     * @param intensity Rumble intensity, between 0 and 1.
     */
    private void writeRumble(XJoystick joystick, double intensity) {

        GenericHID internalJoystick = joystick.getGenericHID();
        isRumbling = intensity > 0;
        if (internalJoystick == null) {
            return;
        }
        internalJoystick.setRumble(RumbleType.kLeftRumble, intensity);
        internalJoystick.setRumble(RumbleType.kRightRumble, intensity);
    }
}
