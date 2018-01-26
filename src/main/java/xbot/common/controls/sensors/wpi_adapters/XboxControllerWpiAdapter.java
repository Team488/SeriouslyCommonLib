package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import edu.wpi.first.wpilibj.XboxController;

public class XboxControllerWpiAdapter extends XXboxController {

    protected XboxController controller;

    @Inject
    public XboxControllerWpiAdapter(@Assisted("port") int port, RobotAssertionManager manager, DevicePolice police) {
        super(port, manager, police);
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
