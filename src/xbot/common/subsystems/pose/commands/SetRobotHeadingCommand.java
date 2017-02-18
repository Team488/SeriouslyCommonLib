package xbot.common.subsystems.pose.commands;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class SetRobotHeadingCommand extends BaseCommand {

    BasePoseSubsystem poseSubsystem;
    double heading;
    
    @Inject
    public SetRobotHeadingCommand(BasePoseSubsystem poseSubsystem) {
        this.poseSubsystem = poseSubsystem;
        heading = BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS;
    }
    
    public void setHeadingToApply(double heading) {
        this.heading = heading;
    }

    @Override
    public void initialize() {
        poseSubsystem.setCurrentHeading(heading);
    }

    @Override
    public void execute() {}
    
    @Override
    public boolean isFinished() {
        return true;
    }
}
