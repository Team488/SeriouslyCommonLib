package xbot.common.controls.sensors;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;

import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.POVDirection;
import org.wpilib.math.geometry.Translation2d;

import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;

/**
 * A platform-neutral abstraction of a Driver Station gamepad.
 */
public abstract class XGamepad extends XJoystick implements IGamepad {

    public static final int LEFT_X_AXIS = 0;
    public static final int LEFT_Y_AXIS = 1;
    public static final int RIGHT_X_AXIS = 2;
    public static final int RIGHT_Y_AXIS = 3;
    public static final int LEFT_TRIGGER_AXIS = 4;
    public static final int RIGHT_TRIGGER_AXIS = 5;

    /**
     * Semantic gamepad buttons, ordered to match WPILib's Gamepad.Button values.
     */
    public enum GamepadButton {
        FACE_DOWN,
        FACE_RIGHT,
        FACE_LEFT,
        FACE_UP,
        BACK,
        GUIDE,
        START,
        LEFT_STICK,
        RIGHT_STICK,
        LEFT_BUMPER,
        RIGHT_BUMPER,
        DPAD_UP,
        DPAD_DOWN,
        DPAD_LEFT,
        DPAD_RIGHT,
        MISC_1,
        RIGHT_PADDLE_1,
        LEFT_PADDLE_1,
        RIGHT_PADDLE_2,
        LEFT_PADDLE_2,
        TOUCHPAD,
        MISC_2,
        MISC_3,
        MISC_4,
        MISC_5,
        MISC_6
    }

    public interface XGamepadFactory {
        XGamepad create(int port);
    }

    private final AdvancedJoystickButtonTriggerFactory joystickButtonFactory;
    private final Map<GamepadButton, AdvancedJoystickButtonTrigger> allocatedButtons;
    private final EnumSet<GamepadButton> reservedButtons;

    protected XGamepad(
            int port,
            AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory,
            AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            RobotAssertionManager assertionManager,
            DevicePolice police) {
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, assertionManager, 0, police);
        this.joystickButtonFactory = joystickButtonFactory;
        this.allocatedButtons = new EnumMap<>(GamepadButton.class);
        this.reservedButtons = EnumSet.noneOf(GamepadButton.class);
    }

    /**
     * Allocates a trigger using a semantic button rather than a device-specific raw button number.
     */
    public AdvancedJoystickButtonTrigger getGamepadButtonIfAvailable(GamepadButton button) {
        Objects.requireNonNull(button, "button");

        if (allocatedButtons.containsKey(button) || reservedButtons.contains(button)) {
            throw new IllegalStateException("Button " + button + " has already been allocated!");
        }

        POVDirection[] directions = getPovDirections(button);
        if (directions.length > 0) {
            assertPovDirectionsAvailable(directions);
        }

        var trigger = joystickButtonFactory.create(this, button.ordinal());
        allocatedButtons.put(button, trigger);
        return trigger;
    }

    @Override
    protected void onPovAllocated(POVDirection direction) {
        EnumSet<GamepadButton> buttons = getGamepadButtons(direction);
        for (GamepadButton button : buttons) {
            if (allocatedButtons.containsKey(button)) {
                throw new IllegalStateException("Button " + button + " has already been allocated!");
            }
        }
        reservedButtons.addAll(buttons);
    }

    private static POVDirection[] getPovDirections(GamepadButton button) {
        return switch (button) {
            case DPAD_UP -> new POVDirection[] {
                POVDirection.UP,
                POVDirection.UP_LEFT,
                POVDirection.UP_RIGHT
            };
            case DPAD_DOWN -> new POVDirection[] {
                POVDirection.DOWN,
                POVDirection.DOWN_LEFT,
                POVDirection.DOWN_RIGHT
            };
            case DPAD_LEFT -> new POVDirection[] {
                POVDirection.LEFT,
                POVDirection.UP_LEFT,
                POVDirection.DOWN_LEFT
            };
            case DPAD_RIGHT -> new POVDirection[] {
                POVDirection.RIGHT,
                POVDirection.UP_RIGHT,
                POVDirection.DOWN_RIGHT
            };
            default -> new POVDirection[0];
        };
    }

    private static EnumSet<GamepadButton> getGamepadButtons(POVDirection direction) {
        return switch (direction) {
            case UP -> EnumSet.of(GamepadButton.DPAD_UP);
            case UP_RIGHT -> EnumSet.of(GamepadButton.DPAD_UP, GamepadButton.DPAD_RIGHT);
            case RIGHT -> EnumSet.of(GamepadButton.DPAD_RIGHT);
            case DOWN_RIGHT -> EnumSet.of(GamepadButton.DPAD_DOWN, GamepadButton.DPAD_RIGHT);
            case DOWN -> EnumSet.of(GamepadButton.DPAD_DOWN);
            case DOWN_LEFT -> EnumSet.of(GamepadButton.DPAD_DOWN, GamepadButton.DPAD_LEFT);
            case LEFT -> EnumSet.of(GamepadButton.DPAD_LEFT);
            case UP_LEFT -> EnumSet.of(GamepadButton.DPAD_UP, GamepadButton.DPAD_LEFT);
            default -> EnumSet.noneOf(GamepadButton.class);
        };
    }

    @Override
    public final boolean getButton(int buttonToken) {
        var buttons = GamepadButton.values();
        if (buttonToken < 0 || buttonToken >= buttons.length) {
            return false;
        }
        return getButton(buttons[buttonToken]);
    }

    public abstract boolean getButton(GamepadButton button);

    public double getLeftStickX() {
        return getLeftX() * (getAxisInverted(LEFT_X_AXIS) ? -1 : 1);
    }

    public double getLeftStickY() {
        return getLeftY() * (getAxisInverted(LEFT_Y_AXIS) ? -1 : 1);
    }

    public double getRightStickX() {
        return getRightX() * (getAxisInverted(RIGHT_X_AXIS) ? -1 : 1);
    }

    public double getRightStickY() {
        return getRightY() * (getAxisInverted(RIGHT_Y_AXIS) ? -1 : 1);
    }

    @Override
    public Translation2d getLeftVector() {
        return new Translation2d(getLeftStickX(), getLeftStickY());
    }

    @Override
    public Translation2d getRightVector() {
        return new Translation2d(getRightStickX(), getRightStickY());
    }

    @Override
    public Translation2d getLeftFieldOrientedVector() {
        return orientForAlliance(new Translation2d(getLeftStickY(), getLeftStickX()));
    }

    @Override
    public Translation2d getRightFieldOrientedVector() {
        return orientForAlliance(new Translation2d(getRightStickY(), getRightStickX()));
    }

    private Translation2d orientForAlliance(Translation2d blueTranslation) {
        if (MatchState.getAlliance().orElse(Alliance.BLUE) == Alliance.BLUE) {
            return blueTranslation;
        }
        return blueTranslation.div(-1);
    }

    @Override
    public void setLeftInversion(boolean xInverted, boolean yInverted) {
        setAxisInverted(LEFT_X_AXIS, xInverted);
        setAxisInverted(LEFT_Y_AXIS, yInverted);
    }

    @Override
    public void setRightInversion(boolean xInverted, boolean yInverted) {
        setAxisInverted(RIGHT_X_AXIS, xInverted);
        setAxisInverted(RIGHT_Y_AXIS, yInverted);
    }

    protected abstract double getLeftX();

    protected abstract double getLeftY();

    protected abstract double getRightX();

    protected abstract double getRightY();
}
