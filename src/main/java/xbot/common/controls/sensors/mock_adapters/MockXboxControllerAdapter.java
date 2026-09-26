package xbot.common.controls.sensors.mock_adapters;

import java.util.EnumMap;

import org.wpilib.driverstation.GenericHID;
import org.wpilib.driverstation.POVDirection;
import org.wpilib.driverstation.XboxController.Axis;
import org.wpilib.driverstation.XboxController.Button;
import org.wpilib.math.util.MathUtil;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;
import xbot.common.subsystems.feedback.XRumbleManager;
import xbot.common.subsystems.feedback.XRumbleManager.XRumbleManagerFactory;

public class MockXboxControllerAdapter extends XXboxController {

    private static final double DEFAULT_STICK_DEADBAND = 0.1;
    private static final double DEFAULT_TRIGGER_DEADBAND = 0.01;

    private XYPair leftStick;
    private XYPair rightStick;

    private double leftTrigger;
    private double rightTrigger;
    private final EnumMap<Button, Boolean> buttons = new EnumMap<>(Button.class);

    private final XRumbleManager rumbleManager;

    @AssistedFactory
    public abstract static class MockXboxControllerFactory implements XXboxControllerFactory {
        public abstract MockXboxControllerAdapter create(@Assisted("port") int port);
    }
    
    @AssistedInject
    public MockXboxControllerAdapter(@Assisted("port") int port,
            AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory,
            AnalogHIDButtonTriggerFactory analogHidButtonFactory, XRumbleManagerFactory rumbleManagerFactory,
            RobotAssertionManager manager, DevicePolice police) {
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, rumbleManagerFactory, manager,
                police);
        leftStick = new XYPair();
        rightStick = new XYPair();
        this.rumbleManager = rumbleManagerFactory.create(this);
    }

    public void setLeftStick(double x, double y) {
        leftStick.x = x * (leftXInversion ? -1 : 1);
        leftStick.y = y * (leftYInversion ? -1 : 1);
    }

    public void setLeftStick(XYPair xy) {
        setLeftStick(xy.x, xy.y);
    }

    public void setRightStick(double x, double y) {
        rightStick.x = x * (rightXInversion ? -1 : 1);
        rightStick.y = y * (rightYInversion ? -1 : 1);
    }

    public void setRightStick(XYPair xy) {
        setRightStick(xy.x, xy.y);
    }

    /**
     * Needed for a few scenarios where we want to emulate the underlying joystick
     * behavior
     * and not the intent after inversion.
     * 
     * @param xy XYPair to directly set. (Remember that by default, most joysticks
     *           have an inverted Y axis!)
     */
    public void setRawLeftStick(XYPair xy) {
        leftStick.x = xy.x;
        leftStick.y = xy.y;
    }

    /**
     * Needed for a few scenarios where we want to emulate the underlying joystick
     * behavior
     * and not the intent after inversion.
     * 
     * @param xy XYPair to directly set. (Remember that by default, most joysticks
     *           have an inverted Y axis!)
     */
    public void setRawRightStick(XYPair xy) {
        rightStick.x = xy.x;
        rightStick.y = xy.y;
    }

    public void setLeftTrigger(double left) {
        leftTrigger = left;
    }

    public void setRightTrigger(double right) {
        rightTrigger = right;
    }

    public void setButton(Button button, boolean pressed) {
        buttons.put(button, pressed);
    }

    @Override
    public boolean getButton(int button) {
        for (Button candidate : Button.values()) {
            if (candidate.value == button) {
                return getButton(candidate);
            }
        }
        return false;
    }

    @Override
    public boolean getButton(Button button) {
        return buttons.getOrDefault(button, false);
    }

    @Override
    public double getRawAxis(int axis) {
        for (Axis candidate : Axis.values()) {
            if (candidate.value == axis) {
                return getAxis(candidate);
            }
        }
        return 0;
    }

    @Override
    public double getAxis(Axis axis) {
        switch (axis) {
            case LEFT_X:
                return leftStick.x;
            case LEFT_Y:
                return leftStick.y;
            case RIGHT_X:
                return rightStick.x;
            case RIGHT_Y:
                return rightStick.y;
            case LEFT_TRIGGER:
                return leftTrigger;
            case RIGHT_TRIGGER:
                return rightTrigger;
            default:
                throw new IllegalArgumentException("Unsupported Xbox axis " + axis);
        }
    }

    @Override
    public POVDirection getPOV() {
        return getPovDirection(
                getButton(Button.DPAD_UP),
                getButton(Button.DPAD_DOWN),
                getButton(Button.DPAD_LEFT),
                getButton(Button.DPAD_RIGHT));
    }

    @Override
    public GenericHID getHID() {
        // We don't have the HID.
        return null;
    }

    @Override
    public XRumbleManager getRumbleManager() {
        return this.rumbleManager;
    }

    @Override
    public double getLeftTrigger() {
        return MathUtil.applyDeadband(leftTrigger, DEFAULT_TRIGGER_DEADBAND);
    }

    @Override
    public double getRightTrigger() {
        return MathUtil.applyDeadband(rightTrigger, DEFAULT_TRIGGER_DEADBAND);
    }

    @Override
    protected double getLeftX() {
        return MathUtil.applyDeadband(leftStick.x, DEFAULT_STICK_DEADBAND);
    }

    @Override
    protected double getLeftY() {
        return MathUtil.applyDeadband(leftStick.y, DEFAULT_STICK_DEADBAND);
    }

    @Override
    protected double getRightX() {
        return MathUtil.applyDeadband(rightStick.x, DEFAULT_STICK_DEADBAND);
    }

    @Override
    protected double getRightY() {
        return MathUtil.applyDeadband(rightStick.y, DEFAULT_STICK_DEADBAND);
    }

}
