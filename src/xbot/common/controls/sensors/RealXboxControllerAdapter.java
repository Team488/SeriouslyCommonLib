package xbot.common.controls.sensors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID.HIDType;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.XboxController;

public class RealXboxControllerAdapter extends XXboxController {

    protected XboxController controller;

    @Inject
    public RealXboxControllerAdapter(@Assisted("port") int port) {
        super(port);
        controller = new XboxController(port);
    }

    @Override
    public int hashCode() {
        return controller.hashCode();
    }

    @Override
    public double getX(Hand hand) {
        return controller.getX(hand);
    }

    @Override
    public double getY(Hand hand) {
        return controller.getY(hand);
    }

    @Override
    public double getRawAxis(int axis) {
        return controller.getRawAxis(axis);
    }

    @Override
    public boolean equals(Object obj) {
        return controller.equals(obj);
    }

    @Override
    public boolean getRawButton(int button) {
        return controller.getRawButton(button);
    }

    @Override
    public double getTriggerAxis(Hand hand) {
        return controller.getTriggerAxis(hand);
    }

    @Override
    public String toString() {
        return controller.toString();
    }
}
