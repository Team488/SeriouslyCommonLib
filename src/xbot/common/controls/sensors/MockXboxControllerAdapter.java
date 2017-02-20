package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.GenericHID.Hand;
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
    
    public MockXboxControllerAdapter(int port) {
        super(port);
        leftStick = new XYPair();
        rightStick = new XYPair();
    }

    @Override
    protected double getTriggerAxis(Hand hand) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected boolean getRawButton(int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected double getRawAxis(int axis) {
        // TODO Auto-generated method stub
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
