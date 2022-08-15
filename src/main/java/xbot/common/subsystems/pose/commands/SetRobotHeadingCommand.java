package xbot.common.subsystems.pose.commands;

import javax.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.pose.BasePoseSubsystem;

public class SetRobotHeadingCommand extends BaseCommand {

    protected final BasePoseSubsystem poseSubsystem;
    private double heading;
    
    @Inject
    public SetRobotHeadingCommand(BasePoseSubsystem poseSubsystem) {
        this.poseSubsystem = poseSubsystem;
        heading = BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS;
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }
    
    public void setHeadingToApply(double heading) {
        this.heading = heading;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        log.info("Setting heading to " + heading);
        poseSubsystem.setCurrentHeading(heading);
    }

    @Override
    public void execute() {}
    
    @Override
    public boolean isFinished() {
        return true;
    }
}
