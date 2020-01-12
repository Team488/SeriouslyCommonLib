package xbot.common.command;

public abstract class BaseSetpointCommand extends BaseCommand {

    public BaseSetpointCommand(SupportsSetpointLock system) {
        this.addRequirements(system.getSetpointLock());
        this.withTimeout(1);
    }
}
