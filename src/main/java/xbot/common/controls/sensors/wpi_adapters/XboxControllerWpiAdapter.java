package xbot.common.controls.sensors.wpi_adapters;

import org.wpilib.driverstation.GenericHID;
import org.wpilib.driverstation.POVDirection;
import org.wpilib.driverstation.XboxController;
import org.wpilib.driverstation.XboxController.Axis;
import org.wpilib.driverstation.XboxController.Button;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.buttons.AdvancedJoystickButtonTrigger.AdvancedJoystickButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AdvancedPovButtonTrigger.AdvancedPovButtonTriggerFactory;
import xbot.common.controls.sensors.buttons.AnalogHIDButtonTrigger.AnalogHIDButtonTriggerFactory;
import xbot.common.injection.DevicePolice;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.subsystems.feedback.XRumbleManager.XRumbleManagerFactory;

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
        return controller.getHID().getRawAxis(axis);
    }

    @Override
    public boolean getButton(int button) {
        return controller.getHID().getRawButton(button);
    }

    @Override
    public boolean getButton(Button button) {
        return controller.getButton(button);
    }

    @Override
    public double getAxis(Axis axis) {
        return controller.getAxis(axis);
    }

    @Override
    public GenericHID getHID() {
        return controller.getHID();
    }

    @Override
    public POVDirection getPOV() {
        return getPovDirection(
                controller.getButton(Button.DPAD_UP),
                controller.getButton(Button.DPAD_DOWN),
                controller.getButton(Button.DPAD_LEFT),
                controller.getButton(Button.DPAD_RIGHT));
    }

    @Override
    public double getLeftTrigger() {
        return controller.getLeftTrigger();
    }

    @Override
    public double getRightTrigger() {
        return controller.getRightTrigger();
    }

    @Override
    protected double getLeftX() {
        return controller.getLeftX();
    }

    @Override
    protected double getLeftY() {
        return controller.getLeftY();
    }

    @Override
    protected double getRightX() {
        return controller.getRightX();
    }

    @Override
    protected double getRightY() {
        return controller.getRightY();
    }
}
