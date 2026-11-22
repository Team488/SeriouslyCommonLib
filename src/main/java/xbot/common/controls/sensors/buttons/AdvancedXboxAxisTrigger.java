package xbot.common.controls.sensors.buttons;

import java.util.HashMap;

import org.wpilib.command2.Command;
import org.wpilib.command2.button.Trigger;

import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.XXboxController.XboxAxisButton;
import xbot.common.controls.sensors.buttons.AdvancedXboxButtonTrigger.ButtonTriggerType;

public class AdvancedXboxAxisTrigger extends AdvancedTrigger {

    private final XXboxController controller;
    private final XboxAxisButton axisButtonName;
    public final HashMap<ButtonTriggerType, Command> triggeredCommands = new HashMap<ButtonTriggerType, Command>();

    public AdvancedXboxAxisTrigger(XXboxController controller, XboxAxisButton buttonName, double threshold) {
        super(() -> getValue(controller, buttonName, threshold));
        this.controller = controller;
        this.axisButtonName = buttonName;
    }

    public XXboxController getController() {
        return controller;
    }

    public XboxAxisButton getAxisButtonName() {
        return axisButtonName;
    }

    @Override
    public Trigger onTrue(final Command command) {
        this.triggeredCommands.put(ButtonTriggerType.WhenPressed, command);
        return super.onTrue(command);
    }

    @Override
    public Trigger onFalse(final Command command) {
        this.triggeredCommands.put(ButtonTriggerType.WhenReleased, command);
        return super.onFalse(command);
    }

    @Override
    public Trigger whileTrue(final Command command) {
        this.triggeredCommands.put(ButtonTriggerType.WhileHeld, command);
        return super.whileTrue(command);
    }

    private static boolean getValue(XXboxController controller, XboxAxisButton buttonName, double threshold) {
        double value = 0;

        switch (buttonName) {
            case LeftTrigger:
                value = controller.getLeftTrigger();
                break;
            case RightTrigger:
                value = controller.getRightTrigger();
                break;
            case LeftJoystickYAxisPositive:
            case LeftJoystickYAxisNegative:
                value = controller.getLeftStickY();
                break;
            case RightJoystickYAxisPositive:
            case RightJoystickYAxisNegative:
                value = controller.getRightStickY();
                break;
            default:
                throw new IllegalArgumentException("Unsupported Xbox axis button " + buttonName);
        }

        if (buttonName.usesNegativeRange()) {
            return value < -threshold;
        }
        return value > threshold;
    }
}
