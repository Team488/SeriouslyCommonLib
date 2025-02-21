package xbot.common.command;

public abstract class BaseSetpointCommand extends BaseCommand {

    public BaseSetpointCommand(SupportsSetpointLock system, SupportsSetpointLock... additionalSystems) {
        // must require at least 1 system
        this.addRequirements(system.getSetpointLock());
        for (SupportsSetpointLock additionalSystem : additionalSystems) {
            this.addRequirements(additionalSystem.getSetpointLock());
        }
    }

    @Override
    public boolean isFinished() {
        // Setpoint commands end instantly by default since the set operation usually
        // happens in initialize and then nothing else needs to happen
        return true;
    }
}
