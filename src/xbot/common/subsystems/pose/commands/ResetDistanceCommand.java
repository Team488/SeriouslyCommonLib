package xbot.common.subsystems.pose.commands;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class ResetDistanceCommand extends BaseCommand {

    BasePoseSubsystem poseSubsystem;
    
    @Inject
    public ResetDistanceCommand(BasePoseSubsystem poseSubsystem) {
        this.poseSubsystem = poseSubsystem;
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
