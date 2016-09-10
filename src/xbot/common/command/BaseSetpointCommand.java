package xbot.common.command;

public abstract class BaseSetpointCommand extends BaseCommand{

    public BaseSetpointCommand(SupportsSetpoint system) {
        requires(system.getSetpointSystem());
    }
}
