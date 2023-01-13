package xbot.common.controls.sensors;

import java.util.HashMap;

import xbot.common.controls.sensors.buttons.AdvancedXboxAxisTrigger;
import xbot.common.controls.sensors.buttons.AdvancedXboxButtonTrigger;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;
import xbot.common.subsystems.feedback.IRumbler;
import xbot.common.subsystems.feedback.XRumbleManager;
import xbot.common.subsystems.feedback.XRumbleManager.XRumbleManagerFactory;

public abstract class XXboxController extends XJoystick implements IRumbler, IGamepad {

    protected final int port;
    protected final RobotAssertionManager assertionManager;

    public final HashMap<XboxButton, AdvancedXboxButtonTrigger> allocatedButtons;

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
        super(port, joystickButtonFactory, advancedPovButtonFactory, analogHidButtonFactory, assertionManager, 10, police);
        this.port = port;
        this.assertionManager = assertionManager;
        allocatedButtons = new HashMap<XboxButton, AdvancedXboxButtonTrigger>();
        rumbleManager = rumbleManagerFactory.create(this);
    }

    @Override
    public XRumbleManager getRumbleManager() {
        return rumbleManager;
    }

    public enum XboxButton {
        A(1), B(2), X(3), Y(4), LeftBumper(5), RightBumper(6), Back(7), Start(8), LeftStick(9), RightStick(10),
        LeftTrigger(-1), RightTrigger(-1), LeftJoystickYAxis(-1), RightJoystickYAxis(-1);

        private int value;

        private XboxButton(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public AdvancedXboxButtonTrigger getifAvailable(XboxButton buttonName) {
        if (!allocatedButtons.containsKey(buttonName)) {
            // If we're trying to use the triggers as buttons, then we need to do some extra
            // work.
            if (buttonName.value == -1) {
                AdvancedXboxAxisTrigger candidate = new AdvancedXboxAxisTrigger(this, buttonName, 0.75);
                allocatedButtons.put(buttonName, candidate);
            } else {
                AdvancedXboxButtonTrigger candidate = new AdvancedXboxButtonTrigger(this, buttonName);
                allocatedButtons.put(buttonName, candidate);
            }
        } else {
            // button already used!
            assertionManager.assertTrue(false, "Button " + buttonName + " has already been allocated!");
        }

        return allocatedButtons.get(buttonName);
    }

    public AdvancedXboxButtonTrigger getXboxButton(XboxButton buttonName) {

        if (!allocatedButtons.containsKey(buttonName)) {
            // key does not exist. Create button!
            AdvancedXboxButtonTrigger candidate;

            // If it's a trigger button, create it in a different way
            if (buttonName.value == -1) {
                candidate = new AdvancedXboxAxisTrigger(this, buttonName, 0.75);
            } else {
                candidate = new AdvancedXboxButtonTrigger(this, buttonName);
            }

            allocatedButtons.put(buttonName, candidate);
        }

        return allocatedButtons.get(buttonName);
    }

    // Joysticks---------------------------------------------------------------------------------------------
    public XYPair getLeftVector() {
        return new XYPair(getLeftStickX(), getLeftStickY());
    }

    public XYPair getRightVector() {
        return new XYPair(getRightStickX(), getRightStickY());
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
        return this.getLeftRawX() * (leftXInversion ? -1 : 1);
    }

    public double getRightStickX() {
        return this.getRightRawX() * (rightXInversion ? -1 : 1);
    }

    public double getLeftStickY() {
        return this.getLeftRawY() * (leftYInversion ? -1 : 1);
    }

    public double getRightStickY() {
        return this.getRightRawY() * (rightYInversion ? -1 : 1);
    }

    // Triggers-----------------------------------------------------------------------------------------------
    public double getLeftTrigger() {
        return this.getLeftRawTriggerAxis();
    }

    public double getRightTrigger() {
        return this.getRightRawTriggerAxis();
    }

    protected abstract double getLeftRawTriggerAxis();

    protected abstract double getRightRawTriggerAxis();

    protected abstract double getLeftRawX();

    protected abstract double getLeftRawY();

    protected abstract double getRightRawX();

    protected abstract double getRightRawY();
}