package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;
import xbot.common.subsystems.feedback.RumbleManager;

public class MockXboxControllerAdapter extends XXboxController {

    private XYPair leftStick;
    private XYPair rightStick;

    private double leftTrigger;
    private double rightTrigger;
    
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

    @Inject
    public MockXboxControllerAdapter(@Assisted("port") int port, CommonLibFactory clf, RobotAssertionManager manager, DevicePolice police) {
        super(port, clf, manager, police);
        leftStick = new XYPair();
        rightStick = new XYPair();
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
    public RumbleManager getRumbleManager() {
        // no actual rumble manager
        return null;
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
