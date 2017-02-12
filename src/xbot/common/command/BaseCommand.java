package xbot.common.command;

import org.apache.log4j.Logger;

import com.google.inject.Inject;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Enhanced version of WPILib's Command that allows for extension
 * of existing functionality.
 */
public abstract class BaseCommand extends Command {

    protected Logger log;
    
    @Inject
    SmartDashboardCommandPutter commandPutter;

    public BaseCommand() {
        log = Logger.getLogger(this.getName());
    }

    public BaseCommand(String name) {
        super(name);
        log = Logger.getLogger(this.getName());
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

    }

    @Override
    public void interrupted() {
        this.end();
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