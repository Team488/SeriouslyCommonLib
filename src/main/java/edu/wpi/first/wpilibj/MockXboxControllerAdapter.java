package edu.wpi.first.wpilibj;

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

    private XYPair leftStick;
    private XYPair rightStick;

    private double leftTrigger;
    private double rightTrigger;

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

    @Override
    public boolean getButton(int button) {
        return false;
    }

    @Override
    public double getRawAxis(int axis) {
        return 0;
    }

    @Override
    public int getPOV() {
        return 0;
    }

    @Override
    public GenericHID getGenericHID() {
        // We don't have the HID.
        return null;
    }

    @Override
    public XRumbleManager getRumbleManager() {
        return this.rumbleManager;
    }

    @Override
    protected double getLeftRawTriggerAxis() {
        return leftTrigger;
    }

    @Override
    protected double getRightRawTriggerAxis() {
        return rightTrigger;
    }

    @Override
    protected double getLeftRawX() {
        return leftStick.x;
    }

    @Override
    protected double getLeftRawY() {
        return leftStick.y;
    }

    @Override
    protected double getRightRawX() {
        return rightStick.x;
    }

    @Override
    protected double getRightRawY() {
        return rightStick.y;
    }

}
