package xbot.common.controls.sensors;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Objects;

import org.wpilib.driverstation.Alliance;
import org.wpilib.driverstation.MatchState;
import org.wpilib.driverstation.POVDirection;
import org.wpilib.driverstation.XboxController.Axis;
import org.wpilib.driverstation.XboxController.Button;
import org.wpilib.math.geometry.Translation2d;

import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedXboxAxisTrigger;
import xbot.common.controls.sensors.buttons.AdvancedXboxButtonTrigger;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.subsystems.feedback.IRumbler;
import xbot.common.subsystems.feedback.XRumbleManager;
import xbot.common.subsystems.feedback.XRumbleManager.XRumbleManagerFactory;

public abstract class XXboxController extends XJoystick implements IRumbler, IGamepad {

    private static final double DEFAULT_AXIS_BUTTON_THRESHOLD = 0.75;

    protected final int port;

    private final EnumMap<Button, AdvancedXboxButtonTrigger> allocatedButtons;
    private final EnumMap<XboxAxisButton, AdvancedXboxAxisTrigger> allocatedAxisButtons;
    private final EnumSet<Button> reservedButtons;

    protected boolean leftXInversion = false;
    protected boolean leftYInversion = false;
    protected boolean rightXInversion = false;
    protected boolean rightYInversion = false;

    protected final XRumbleManager rumbleManager;

    public interface XXboxControllerFactory {
        XXboxController create(int port);
    }

    protected XXboxController(int port, AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory advancedPovButtonFactory, AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            XRumbleManagerFactory rumbleManagerFactory, RobotAssertionManager assertionManager,
            DevicePolice police) {
        super(port, joystickButtonFactory, advancedPovButtonFactory, analogHidButtonFactory, assertionManager, 0,
                police);
        this.port = port;
        allocatedButtons = new EnumMap<>(Button.class);
        allocatedAxisButtons = new EnumMap<>(XboxAxisButton.class);
        reservedButtons = EnumSet.noneOf(Button.class);
        rumbleManager = rumbleManagerFactory.create(this);
    }

    @Override
    public XRumbleManager getRumbleManager() {
        return rumbleManager;
    }

    /**
     * Xbox names for WPILib's typed, zero-based physical buttons.
     */
    public static final class XboxButton {
        public static final Button A = Button.A;
        public static final Button B = Button.B;
        public static final Button X = Button.X;
        public static final Button Y = Button.Y;
        public static final Button View = Button.VIEW;
        public static final Button Xbox = Button.XBOX;
        public static final Button Menu = Button.MENU;
        public static final Button LeftStick = Button.LEFT_STICK;
        public static final Button RightStick = Button.RIGHT_STICK;
        public static final Button LeftBumper = Button.LEFT_BUMPER;
        public static final Button RightBumper = Button.RIGHT_BUMPER;
        public static final Button DPadUp = Button.DPAD_UP;
        public static final Button DPadDown = Button.DPAD_DOWN;
        public static final Button DPadLeft = Button.DPAD_LEFT;
        public static final Button DPadRight = Button.DPAD_RIGHT;

        private XboxButton() {
        }
    }

    public enum XboxAxisButton {
        LeftTrigger(Axis.LEFT_TRIGGER, false),
        RightTrigger(Axis.RIGHT_TRIGGER, false),
        LeftJoystickYAxisPositive(Axis.LEFT_Y, false),
        RightJoystickYAxisPositive(Axis.RIGHT_Y, false),
        LeftJoystickYAxisNegative(Axis.LEFT_Y, true),
        RightJoystickYAxisNegative(Axis.RIGHT_Y, true);

        private final Axis axis;
        private final boolean usesNegativeRange;

        XboxAxisButton(Axis axis, boolean usesNegativeRange) {
            this.axis = axis;
            this.usesNegativeRange = usesNegativeRange;
        }

        public Axis getAxis() {
            return axis;
        }

        public boolean usesNegativeRange() {
            return usesNegativeRange;
        }
    }

    public AdvancedXboxButtonTrigger getXboxButtonIfAvailable(Button button) {
        Objects.requireNonNull(button, "button");

        if (allocatedButtons.containsKey(button) || reservedButtons.contains(button)) {
            throw new IllegalStateException("Xbox button " + button + " has already been allocated!");
        }

        POVDirection[] directions = getPovDirections(button);
        if (directions.length > 0) {
            assertPovDirectionsAvailable(directions);
        }

        AdvancedXboxButtonTrigger candidate = new AdvancedXboxButtonTrigger(this, button);
        allocatedButtons.put(button, candidate);
        return candidate;
    }

    @Override
    protected void onPovAllocated(POVDirection direction) {
        EnumSet<Button> buttons = getXboxButtons(direction);
        for (Button button : buttons) {
            if (allocatedButtons.containsKey(button)) {
                throw new IllegalStateException("Xbox button " + button + " has already been allocated!");
            }
        }
        reservedButtons.addAll(buttons);
    }

    private static POVDirection[] getPovDirections(Button button) {
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

    private static EnumSet<Button> getXboxButtons(POVDirection direction) {
        return switch (direction) {
            case UP -> EnumSet.of(Button.DPAD_UP);
            case UP_RIGHT -> EnumSet.of(Button.DPAD_UP, Button.DPAD_RIGHT);
            case RIGHT -> EnumSet.of(Button.DPAD_RIGHT);
            case DOWN_RIGHT -> EnumSet.of(Button.DPAD_DOWN, Button.DPAD_RIGHT);
            case DOWN -> EnumSet.of(Button.DPAD_DOWN);
            case DOWN_LEFT -> EnumSet.of(Button.DPAD_DOWN, Button.DPAD_LEFT);
            case LEFT -> EnumSet.of(Button.DPAD_LEFT);
            case UP_LEFT -> EnumSet.of(Button.DPAD_UP, Button.DPAD_LEFT);
            default -> EnumSet.noneOf(Button.class);
        };
    }

    public AdvancedXboxAxisTrigger getXboxAxisButtonIfAvailable(XboxAxisButton button) {
        Objects.requireNonNull(button, "button");

        if (allocatedAxisButtons.containsKey(button)) {
            throw new IllegalStateException("Xbox axis button " + button + " has already been allocated!");
        }

        AdvancedXboxAxisTrigger candidate = new AdvancedXboxAxisTrigger(this, button,
                DEFAULT_AXIS_BUTTON_THRESHOLD);
        allocatedAxisButtons.put(button, candidate);
        return candidate;
    }

    // Joysticks---------------------------------------------------------------------------------------------
    public Translation2d getLeftVector() {
        return new Translation2d(getLeftStickX(), getLeftStickY());
    }

    public Translation2d getRightVector() {
        return new Translation2d(getRightStickX(), getRightStickY());
    }

    public Translation2d getLeftFieldOrientedVector() {
        var blueTranslation = new Translation2d(getLeftStickY(), getLeftStickX());
        if (MatchState.getAlliance().orElseGet(() -> Alliance.BLUE) == Alliance.BLUE) {
            return blueTranslation;
        } else {
            // when on red, both axis invert
            return blueTranslation.div(-1);
        }
    }

    public Translation2d getRightFieldOrientedVector() {
        var blueTranslation = new Translation2d(getRightStickY(), getRightStickX());
        if (MatchState.getAlliance().orElseGet(() -> Alliance.BLUE) == Alliance.BLUE) {
            return blueTranslation;
        } else {
            // when on red, both axis invert
            return blueTranslation.div(-1);
        }
    }

    public void setLeftInversion(boolean xInverted, boolean yInverted) {
        leftXInversion = xInverted;
        leftYInversion = yInverted;
    }

    public void setRightInversion(boolean xInverted, boolean yInverted) {
        rightXInversion = xInverted;
        rightYInversion = yInverted;
    }

    public double getLeftStickX() {
        return this.getLeftX() * (leftXInversion ? -1 : 1);
    }

    public double getRightStickX() {
        return this.getRightX() * (rightXInversion ? -1 : 1);
    }

    public double getLeftStickY() {
        return this.getLeftY() * (leftYInversion ? -1 : 1);
    }

    public double getRightStickY() {
        return this.getRightY() * (rightYInversion ? -1 : 1);
    }

    public abstract boolean getButton(Button button);

    public abstract double getAxis(Axis axis);

    protected abstract double getLeftX();

    protected abstract double getLeftY();

    protected abstract double getRightX();

    protected abstract double getRightY();
}
