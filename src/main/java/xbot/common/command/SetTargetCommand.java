package xbot.common.command;

public class SetTargetCommand<T> extends BaseSetpointCommand {

    protected T targetValue;
    protected final BaseSetpointSubsystem<T> system;

    public SetTargetCommand(BaseSetpointSubsystem<T> system) {
        super(system);

        this.system = system;
    }

    public void setTargetValue(T value) {
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
