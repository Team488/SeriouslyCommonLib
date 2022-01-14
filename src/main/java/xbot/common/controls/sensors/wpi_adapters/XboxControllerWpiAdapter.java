package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;

public class XboxControllerWpiAdapter extends XXboxController {

    protected XboxController controller;

    @Inject
    public XboxControllerWpiAdapter(@Assisted("port") int port, CommonLibFactory clf, RobotAssertionManager manager, DevicePolice police) {
        super(port, clf, manager, police);
        controller = new XboxController(port);
    }

    @Override
    public double getRawAxis(int axis) {
        return controller.getRawAxis(axis);
    }

    @Override
    public boolean getButton(int button) {
        return controller.getRawButton(button);
    }

    @Override
    public GenericHID getGenericHID() {
        return controller;
    }

    @Override
    public int getPOV() {
        return controller.getPOV();
    }

    @Override
    protected double getLeftRawTriggerAxis() {
        return controller.getLeftTriggerAxis();
    }

    @Override
    protected double getRightRawTriggerAxis() {
        return controller.getRightTriggerAxis();
    }

    @Override
    protected double getLeftRawX() {
        return controller.getLeftX();
    }

    @Override
    protected double getLeftRawY() {
        return controller.getLeftY();
    }

    @Override
    protected double getRightRawX() {
        return controller.getRightX();
    }

    @Override
    protected double getRightRawY() {
        return controller.getRightY();
    }
}
