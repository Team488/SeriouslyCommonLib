package xbot.common.controls.sensors;

import java.util.HashMap;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Button;
import xbot.common.controls.sensors.XXboxController.XboxButton;

public class AdvancedXboxButton extends AdvancedButton {

    public enum ButtonTriggerType {
        WhenPressed(0), WhileHeld(1), WhenReleased(2);

        private int value;

        private ButtonTriggerType(final int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static final Logger log = Logger.getLogger(AdvancedButton.class);

    XXboxController controller;
    public XboxButton buttonName;

    public final HashMap<ButtonTriggerType, Command> triggeredCommands = new HashMap<ButtonTriggerType, Command>();

    public AdvancedXboxButton(final XXboxController controller, final XboxButton buttonName) {
        log.info("Creating XboxButton " + buttonName.toString());// + " on port " + controller.getInternalController().getPort());
        this.controller = controller;
        this.buttonName = buttonName;
    }

    @Override
    public boolean get() {
        return controller.getButton(this.buttonName.getValue());
    }

    @Override
    public Button whenPressed(final Command command) {
        this.triggeredCommands.put(ButtonTriggerType.WhenPressed, command);
        return super.whenPressed(command);
    }

    @Override
    public Button whenReleased(final Command command) {
        this.triggeredCommands.put(ButtonTriggerType.WhenReleased, command);
        return super.whenReleased(command);
    }

    @Override
    public Button whileHeld(final Command command) {
        this.triggeredCommands.put(ButtonTriggerType.WhileHeld, command);
        return super.whileHeld(command);
    }
}