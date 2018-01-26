package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;

public class MockXboxControllerAdapter extends XXboxController {

    private XYPair leftStick;
    private XYPair rightStick;
    
    public void setLeftStick(double x, double y) {
        leftStick.x = x;
        leftStick.y = y;
    }
    
    public void setRightStick(double x, double y) {
        rightStick.x = x;
        rightStick.y = y;
    }
    
    @Inject
    public MockXboxControllerAdapter(@Assisted("port") int port, RobotAssertionManager manager, DevicePolice police) {
        super(port, manager, police);
        leftStick = new XYPair();
        rightStick = new XYPair();
    }

    @Override
    protected double getTriggerAxis(Hand hand) {
        return 0;
    }

    @Override
    protected boolean getRawButton(int button) {
        return false;
    }

    @Override
    protected double getRawAxis(int axis) {
        return 0;
    }

    @Override
    protected double getY(Hand hand) {
        if (hand == Hand.kLeft) {
            return leftStick.y;
        }
        return rightStick.y;
    }

    @Override
    protected double getX(Hand hand) {
        if (hand == Hand.kLeft) {
            return leftStick.x;
        }
        return rightStick.x;
    }
}
