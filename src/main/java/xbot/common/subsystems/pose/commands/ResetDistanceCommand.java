package xbot.common.subsystems.pose.commands;

import javax.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class ResetDistanceCommand extends BaseCommand {

    protected final BasePoseSubsystem poseSubsystem;
    
    @Inject
    public ResetDistanceCommand(BasePoseSubsystem poseSubsystem) {
        this.poseSubsystem = poseSubsystem;
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        poseSubsystem.resetDistanceTraveled();
    }

    @Override
    public void execute() {
        
    }
    
    @Override
    public boolean isFinished() {
        return true;
    }
}
