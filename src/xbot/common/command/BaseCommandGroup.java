package xbot.common.command;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class BaseCommandGroup extends CommandGroup {
    @Inject
    SmartDashboardCommandPutter commandPutter;
    
    public void includeOnSmartDashboard () {
        if(commandPutter != null) {
            commandPutter.addCommandToSmartDashboard(this);
        }
    }
}