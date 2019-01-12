package xbot.common.command;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import xbot.common.logging.TimeLogger;

/**
 * Enhanced version of WPILib's Command that allows for extension
 * of existing functionality.
 */
public abstract class BaseCommand extends Command {

    protected Logger log;
    protected TimeLogger monitor;
    
    @Inject
    SmartDashboardCommandPutter commandPutter;

    public BaseCommand() {
        log = Logger.getLogger(this.getName());
        monitor = new TimeLogger(this.getName(), 20);
    }

    public BaseCommand(String name) {
        super(name);
        log = Logger.getLogger(this.getName());
    }
    
    public String getPrefix() {
        return this.getName() + "/";
    }

    @Override
    public abstract void initialize();

    @Override
    public abstract void execute();

    @Override
    public boolean isFinished() {
        return false;
    }

    @Override
    public void end() {
        log.info("Ending");
    }

    @Override
    public void interrupted() {
        log.info("Interrupted");
        end();
    }

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