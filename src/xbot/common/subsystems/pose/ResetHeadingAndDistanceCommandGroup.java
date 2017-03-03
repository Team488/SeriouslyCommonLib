package xbot.common.subsystems.pose;

import edu.wpi.first.wpilibj.command.CommandGroup;
import xbot.common.properties.XPropertyManager;
import xbot.common.subsystems.pose.commands.ResetDistanceCommand;
import xbot.common.subsystems.pose.commands.SetRobotHeadingCommand;

public class ResetHeadingAndDistanceCommandGroup extends CommandGroup {
    
    @Inject
    public ResetHeadingAndDistanceCommandGroup(XPropertyManager propManager, 
            BasePoseSubsystem poseSubsystem,
            ResetDistanceCommand resetDistanceCommand,
            SetRobotHeadingCommand setRobotHeadingCommand) {
        ResetDistanceCommand resetDistance = 
                new ResetDistanceCommand(resetDistanceCommand.);
        SetRobotHeadingCommand resetHeading =
                new SetRobotHeadingCommand(setRobotHeadingCommand.setHeadingToApply(0));
        
        this.addParallel(resetDistance);
        this.addParallel(resetHeading);
    }
}
