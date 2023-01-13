package xbot.common.controls.sensors.wpi_adapters;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.subsystems.feedback.XRumbleManager.XRumbleManagerFactory;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;

public class XboxControllerWpiAdapter extends XXboxController {

    protected XboxController controller;

    @AssistedFactory
    public abstract static class XboxControllerWpiAdapterFactory implements XXboxControllerFactory {
        public abstract XboxControllerWpiAdapter create(@Assisted("port") int port);
    }

    @AssistedInject
    public XboxControllerWpiAdapter(@Assisted("port") int port, AdvancedJoystickButtonTriggerFactory joystickButtonFactory,
            AdvancedPovButtonTriggerFactory povButtonFactory, AnalogHIDButtonTriggerFactory analogHidButtonFactory,
            XRumbleManagerFactory rumbleManagerFactory, RobotAssertionManager manager, DevicePolice police) {
        super(port, joystickButtonFactory, povButtonFactory, analogHidButtonFactory, rumbleManagerFactory, manager,
                police);
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
