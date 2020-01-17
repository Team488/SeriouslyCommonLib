package xbot.common.command;

public abstract class BaseSetpointCommand extends BaseCommand {

    public BaseSetpointCommand(SupportsSetpointLock system) {
        this.addRequirements(system.getSetpointLock());
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
