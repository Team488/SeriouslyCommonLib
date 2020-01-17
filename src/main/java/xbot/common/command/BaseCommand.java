package xbot.common.command;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import xbot.common.logging.TimeLogger;

/**
 * Enhanced version of WPILib's Command that allows for extension of existing
 * functionality.
 */
public abstract class BaseCommand extends CommandBase {

    protected Logger log;
    protected TimeLogger monitor;
    private boolean configurableRunWhenDisabled;
    
    @Inject
    SmartDashboardCommandPutter commandPutter;

    public BaseCommand() {
        log = Logger.getLogger(this.getName());
        monitor = new TimeLogger(this.getName(), 20);
    }

    @Override
    public boolean runsWhenDisabled() {
        return configurableRunWhenDisabled;
    }

    public void setRunsWhenDisabled(boolean value) {
        configurableRunWhenDisabled = value;
    }
    
    public String getPrefix() {
        return this.getName() + "/";
    }

    @Override
    public abstract void initialize();

    @Override
    public abstract void execute();    

    public void includeOnSmartDashboard() {
        if (commandPutter != null) {
            commandPutter.addCommandToSmartDashboard(this);
        }
    }

    public void includeOnSmartDashboard(String label) {
        if (commandPutter != null) {
            commandPutter.addCommandToSmartDashboard(label, this);
        }
    }

}