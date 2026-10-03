package xbot.common.controls.sensors.wpi_adapters;

import org.wpilib.driverstation.Gamepad;
import org.wpilib.driverstation.GenericHID;
import org.wpilib.driverstation.POVDirection;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XGamepad;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;

public class GamepadWpiAdapter extends XGamepad {

    private final Gamepad internalGamepad;

    @AssistedFactory
    public abstract static class GamepadWpiAdapterFactory implements XGamepadFactory {
        public abstract GamepadWpiAdapter create(@Assisted("port") int port);
    }

    @AssistedInject
    public GamepadWpiAdapter(
            @Assisted("port") int port,
            AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory,
            AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            RobotAssertionManager assertionManager,
            DevicePolice police) {
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, assertionManager, police);
        internalGamepad = new Gamepad(port);
    }

    @Override
    public double getRawAxis(int axisNumber) {
        return internalGamepad.getHID().getRawAxis(axisNumber);
    }

    @Override
    public boolean getButton(GamepadButton button) {
        return internalGamepad.getButton(toWpiButton(button));
    }

    @Override
    public double getLeftTrigger() {
        return internalGamepad.getLeftTrigger();
    }

    @Override
    public double getRightTrigger() {
        return internalGamepad.getRightTrigger();
    }

    @Override
    protected double getLeftX() {
        return internalGamepad.getLeftX();
    }

    @Override
    protected double getLeftY() {
        return internalGamepad.getLeftY();
    }

    @Override
    protected double getRightX() {
        return internalGamepad.getRightX();
    }

    @Override
    protected double getRightY() {
        return internalGamepad.getRightY();
    }

    private Gamepad.Button toWpiButton(GamepadButton button) {
        return switch (button) {
            case FACE_DOWN -> Gamepad.Button.FACE_DOWN;
            case FACE_RIGHT -> Gamepad.Button.FACE_RIGHT;
            case FACE_LEFT -> Gamepad.Button.FACE_LEFT;
            case FACE_UP -> Gamepad.Button.FACE_UP;
            case BACK -> Gamepad.Button.BACK;
            case GUIDE -> Gamepad.Button.GUIDE;
            case START -> Gamepad.Button.START;
            case LEFT_STICK -> Gamepad.Button.LEFT_STICK;
            case RIGHT_STICK -> Gamepad.Button.RIGHT_STICK;
            case LEFT_BUMPER -> Gamepad.Button.LEFT_BUMPER;
            case RIGHT_BUMPER -> Gamepad.Button.RIGHT_BUMPER;
            case DPAD_UP -> Gamepad.Button.DPAD_UP;
            case DPAD_DOWN -> Gamepad.Button.DPAD_DOWN;
            case DPAD_LEFT -> Gamepad.Button.DPAD_LEFT;
            case DPAD_RIGHT -> Gamepad.Button.DPAD_RIGHT;
            case MISC_1 -> Gamepad.Button.MISC_1;
            case RIGHT_PADDLE_1 -> Gamepad.Button.RIGHT_PADDLE_1;
            case LEFT_PADDLE_1 -> Gamepad.Button.LEFT_PADDLE_1;
            case RIGHT_PADDLE_2 -> Gamepad.Button.RIGHT_PADDLE_2;
            case LEFT_PADDLE_2 -> Gamepad.Button.LEFT_PADDLE_2;
            case TOUCHPAD -> Gamepad.Button.TOUCHPAD;
            case MISC_2 -> Gamepad.Button.MISC_2;
            case MISC_3 -> Gamepad.Button.MISC_3;
            case MISC_4 -> Gamepad.Button.MISC_4;
            case MISC_5 -> Gamepad.Button.MISC_5;
            case MISC_6 -> Gamepad.Button.MISC_6;
            default -> throw new IllegalArgumentException("Unsupported gamepad button: " + button);
        };
    }

    @Override
    public POVDirection getPOV() {
        return getPovDirection(
                internalGamepad.getButton(Gamepad.Button.DPAD_UP),
                internalGamepad.getButton(Gamepad.Button.DPAD_DOWN),
                internalGamepad.getButton(Gamepad.Button.DPAD_LEFT),
                internalGamepad.getButton(Gamepad.Button.DPAD_RIGHT));
    }

    @Override
    public GenericHID getHID() {
        return internalGamepad.getHID();
    }
}
