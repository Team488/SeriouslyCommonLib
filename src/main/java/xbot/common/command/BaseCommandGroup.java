package xbot.common.command;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class BaseCommandGroup extends CommandGroup {
    
    protected Logger log;
    
    @Inject
    SmartDashboardCommandPutter commandPutter;
    
    public BaseCommandGroup() {
        log = Logger.getLogger(this.getName());
    }

    public BaseCommandGroup(String name) {
        super(name);
        log = Logger.getLogger(this.getName());
    }
    
    public void includeOnSmartDashboard () {
        if(commandPutter != null) {
            commandPutter.addCommandToSmartDashboard(this);
        }
    }
    
    public void includeOnSmartDashboard(String label) {
        if (commandPutter != null) {
            commandPutter.addCommandToSmartDashboard(label, this);
        }
    }
    
    @Override
    protected void interrupted() {
        log.info("Interrupted");
        end();
    }
    
}