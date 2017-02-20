package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.GenericHID.HIDType;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import xbot.common.math.XYPair;

public class MockXboxControllerAdapter extends BaseXboxControllerAdapter {

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
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setRumble(RumbleType type, double value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setOutputs(int value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setOutput(int outputNumber, boolean value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HIDType getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPOVCount() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public int getPOV() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getPOV(int pov) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getStartButton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getBackButton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getStickButton(Hand hand) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean getStickButton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getYButton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getXButton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getBButton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getAButton() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double getTriggerAxis(Hand hand) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getRawButton(int button) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getTop(Hand hand) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getPort() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getTrigger(Hand hand) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getBumper(Hand hand) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean getBumper() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double getRawAxis(int axis) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getY(Hand hand) {
        if (hand == Hand.kLeft) {
            return leftStick.y;
        }
        return rightStick.y;
    }

    @Override
    public double getY() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getX(Hand hand) {
        if (hand == hand.kLeft) {
            return leftStick.x;
        }
        return rightStick.x;
    }

    @Override
    public double getX() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }
}
