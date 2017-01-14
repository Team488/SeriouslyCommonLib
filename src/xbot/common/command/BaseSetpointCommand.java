package xbot.common.command;

public abstract class BaseSetpointCommand extends BaseCommand {

    public BaseSetpointCommand(SupportsSetpointLock system) {
        requires(system.getSetpointLock());
    }
}
