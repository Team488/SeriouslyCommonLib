package xbot.common.subsystems.pose;

import com.google.inject.Inject;

import xbot.common.command.BaseCommandGroup;
import xbot.common.subsystems.pose.commands.ResetDistanceCommand;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;

public class ResetHeadingAndDistanceCommandGroup extends BaseCommandGroup {
    
    @Inject
    public ResetHeadingAndDistanceCommandGroup(BasePoseSubsystem poseSubsystem,
            ResetDistanceCommand resetDistanceCommand,
            SetRobotHeadingCommand setRobotHeadingCommand) {
        ResetDistanceCommand resetDistance = 
                new ResetDistanceCommand(poseSubsystem);
        SetRobotHeadingCommand resetHeading =
                new SetRobotHeadingCommand(poseSubsystem);
        
        this.addParallel(resetDistance);
        this.addParallel(resetHeading);
    }
}
