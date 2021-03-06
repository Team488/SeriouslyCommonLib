package xbot.common.controls.sensors;

import java.util.HashMap;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;
import xbot.common.subsystems.feedback.IRumbler;
import xbot.common.subsystems.feedback.RumbleManager;

public abstract class XXboxController extends XJoystick implements IRumbler, IGamepad {

    protected int port;
    RobotAssertionManager assertionManager;

    public HashMap<XboxButton, AdvancedXboxButton> allocatedButtons;

    boolean leftXInversion = false;
    boolean leftYInversion = false;
    boolean rightXInversion = false;
    boolean rightYInversion = false;

    RumbleManager rumbleManager;

    @Inject
    public XXboxController(int port, CommonLibFactory clf, RobotAssertionManager assertionManager,
            DevicePolice police) {
        super(port, clf, assertionManager, 10, police);
        this.port = port;
        this.assertionManager = assertionManager;
        allocatedButtons = new HashMap<XboxButton, AdvancedXboxButton>();
        rumbleManager = clf.createRumbleManager(this);
    }

    @Override
    public RumbleManager getRumbleManager() {
        return rumbleManager;
    }

    public enum XboxButton {
        A(1), B(2), X(3), Y(4), LeftBumper(5), RightBumper(6), Back(7), Start(8), LeftStick(9), RightStick(10),
        LeftTrigger(-1), RightTrigger(-1);

        private int value;

        private XboxButton(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public AdvancedXboxButton getifAvailable(XboxButton buttonName) {
        if (!allocatedButtons.containsKey(buttonName)) {
            // If we're trying to use the triggers as buttons, then we need to do some extra work.
            if (buttonName == XboxButton.LeftTrigger || buttonName == XboxButton.RightTrigger) {
                AdvancedXboxAxisButton candidate = new AdvancedXboxAxisButton(this, buttonName, 0.75);
                allocatedButtons.put(buttonName, candidate);
            } else {
                AdvancedXboxButton candidate = new AdvancedXboxButton(this, buttonName);
                allocatedButtons.put(buttonName, candidate);
            }
        }
        else {
            // button already used!
            assertionManager.assertTrue(false, "Button " + buttonName + " has already been allocated!");
        }

        return allocatedButtons.get(buttonName);
    }

    public AdvancedXboxButton getXboxButton(XboxButton buttonName) {

        if (!allocatedButtons.containsKey(buttonName)) {
            // key does not exist. Create button!
            AdvancedXboxButton candidate;

            // If it's a trigger button, create it in a different way
            if (buttonName == XboxButton.LeftTrigger || buttonName == XboxButton.RightTrigger) {
                candidate = new AdvancedXboxAxisButton(this, buttonName, 0.75);
            } else {
                candidate = new AdvancedXboxButton(this, buttonName);
            }

            allocatedButtons.put(buttonName, candidate);
        } else {
            // key exists!
            assertionManager.assertTrue(false, "Button " + buttonName + " has already been allocated!");
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
        return this.getX(Hand.kLeft) * (leftXInversion ? -1 : 1);
    }

    public double getRightStickX() {
        return this.getX(Hand.kRight) * (rightXInversion ? -1 : 1);
    }

    public double getLeftStickY() {
        return this.getY(Hand.kLeft) * (leftYInversion ? -1 : 1);
    }

    public double getRightStickY() {
        return this.getY(Hand.kRight) * (rightYInversion ? -1 : 1);
    }

    // Triggers-----------------------------------------------------------------------------------------------
    public double getLeftTrigger() {
        return this.getTriggerAxis(Hand.kLeft);
    }

    public double getRightTrigger() {
        return this.getTriggerAxis(Hand.kRight);
    }

    protected abstract double getTriggerAxis(Hand hand);

    protected abstract double getY(Hand hand);

    protected abstract double getX(Hand hand);
}