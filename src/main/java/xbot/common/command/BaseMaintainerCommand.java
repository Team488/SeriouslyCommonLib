package xbot.common.command;

public abstract class BaseMaintainerCommand extends BaseCommand {

    BaseSetpointSubsystem subsystemToMaintan;

    /**
     * 
     * @param subsystemToMaintain
     */
    public BaseMaintainerCommand(BaseSetpointSubsystem subsystemToMaintain) {
        this.subsystemToMaintan = subsystemToMaintain;
        this.addRequirements(subsystemToMaintain);
    }

    @Override
    public void execute() {
        maintain();
        subsystemToMaintan.setMaintainerIsAtGoal(isMaintainerAtGoal());
    }

    /**
     * Contains all the logic associated with keeping the subsystem
     * at its goal.
     */
    protected abstract void maintain();

    protected abstract boolean isMaintainerAtGoal();
}