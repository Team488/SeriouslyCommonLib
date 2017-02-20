package xbot.common.controls.sensors;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.XboxController;

public class RealXboxControllerAdapter extends XXboxController {

    protected XboxController controller;

    @Inject
    public RealXboxControllerAdapter(@Assisted("port") int port) {
        super(port);
        controller = new XboxController(port);
    }

    @Override
    protected double getX(Hand hand) {
        return controller.getX(hand);
    }

    @Override
    protected double getY(Hand hand) {
        return controller.getY(hand);
    }

    @Override
    protected double getRawAxis(int axis) {
        return controller.getRawAxis(axis);
    }

    @Override
    protected boolean getRawButton(int button) {
        return controller.getRawButton(button);
    }

    @Override
    protected double getTriggerAxis(Hand hand) {
        return controller.getTriggerAxis(hand);
    }
}
