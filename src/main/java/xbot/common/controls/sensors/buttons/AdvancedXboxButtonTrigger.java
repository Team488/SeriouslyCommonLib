package xbot.common.controls.sensors.buttons;

import java.util.HashMap;
import java.util.function.BooleanSupplier;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.controls.sensors.XXboxController.XboxButton;

public class AdvancedXboxButtonTrigger extends AdvancedTrigger {

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

    private static final Logger log = Logger.getLogger(AdvancedTrigger.class);

    XXboxController controller;
    public XboxButton buttonName;

    public final HashMap<ButtonTriggerType, Command> triggeredCommands = new HashMap<ButtonTriggerType, Command>();

    public AdvancedXboxButtonTrigger(final XXboxController controller, final XboxButton buttonName) {
        this(controller, buttonName, () -> controller.getButton(buttonName.getValue()));
    }

    protected AdvancedXboxButtonTrigger(final XXboxController controller, final XboxButton buttonName, final BooleanSupplier supplier) {
        super(supplier);
        log.info("Creating XboxButton " + buttonName.toString());// + " on port " + controller.getInternalController().getPort());
        this.controller = controller;
        this.buttonName = buttonName;
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
}