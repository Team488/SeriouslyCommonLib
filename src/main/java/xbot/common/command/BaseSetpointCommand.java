package xbot.common.command;

public abstract class BaseSetpointCommand extends BaseCommand {

    public BaseSetpointCommand(SupportsSetpointLock system) {
        requires(system.getSetpointLock());
        this.setTimeout(1);
    }
    
    @Override
    public boolean isFinished() {
        return this.isTimedOut();
    }
}
