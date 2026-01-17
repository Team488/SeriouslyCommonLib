package xbot.common.command;

/**
 * A command that sets a target value on a BaseSetpointSubsystem.
 * @param <TargetT> The type of the target value.
 */
public class SetTargetCommand<TargetT> extends BaseSetpointCommand {

    protected TargetT targetValue;
    protected final BaseSetpointSubsystem<TargetT, ?> system;

    /**
     * Creates a new SetTargetCommand.
     * @param system The BaseSetpointSubsystem to set the target value on.
     */
    public SetTargetCommand(BaseSetpointSubsystem<TargetT, ?> system) {
        super(system);

        this.system = system;
    }

    /**
     * Sets the target value to be applied when the command is initialized.
     * @param value The target value to set.
     */
    public void setTargetValue(TargetT value) {
        this.targetValue = value;
    }

    @Override
    public void initialize() {
        system.setTargetValue(this.targetValue);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
