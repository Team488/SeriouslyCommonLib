package xbot.common.command;

import javax.inject.Inject;

import edu.wpi.first.wpilibj2.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xbot.common.advantage.AKitLogger;
import xbot.common.logging.TimeLogger;
import xbot.common.properties.IPropertySupport;

/**
 * Enhanced version of WPILib's Command that allows for extension of existing
 * functionality.
 */
public abstract class BaseCommand extends Command implements IPropertySupport {

    protected final Logger log;
    protected final AKitLogger aKitLog;
    protected final TimeLogger monitor;
    private boolean configurableRunWhenDisabled;
    
    @Inject
    SmartDashboardCommandPutter commandPutter;

    public BaseCommand() {
        log = LogManager.getLogger(this.getName());
        aKitLog = new AKitLogger(this);
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

    /**
     * @deprecated
     * Suggest use {@link #addRequirements(edu.wpi.first.wpilibj2.command.Subsystem...)} instead.
     * @param subsystem Requirement to add
     */
    @Deprecated
    public void requires(BaseSubsystem subsystem) {
        this.addRequirements(subsystem);
    }

}