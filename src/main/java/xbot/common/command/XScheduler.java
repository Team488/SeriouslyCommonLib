package xbot.common.command;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.wpi.first.wpilibj.Alert;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;

/**
 * Wrapper for base Scheduler which intelligently manages exceptions.
 */
@Singleton
public class XScheduler {

    private static Logger log = LogManager.getLogger(XScheduler.class);

    boolean crashedPreviously = false;

    int numberOfCrashes = 0;

    Throwable lastException = null;

    final Alert schedulerCrashedAlert;
    final CommandScheduler scheduler;

    @Inject
    public XScheduler() {
        this.schedulerCrashedAlert = new Alert("Scheduler Crashed", Alert.AlertType.kError);
        this.scheduler = CommandScheduler.getInstance();
    }

    public int getNumberOfCrashes()
    {
        return numberOfCrashes;
    }

    public void run() {
        try {
            scheduler.run();
            crashedPreviously = false;
            schedulerCrashedAlert.set(false);
            lastException = null;
        } catch(Throwable t) {
            var alertText = String.format(
                    "Unhandled exception in scheduler: %s\n%s",
                    t.toString(),
                    Arrays.toString(t.getStackTrace()));
            log.error(alertText);
            schedulerCrashedAlert.setText(alertText);
            schedulerCrashedAlert.set(true);
            lastException = t;
            if(crashedPreviously) {
                log.error("Due to repeated exceptions, clearing Scheduler queue completely");
                scheduler.cancelAll();
            }
            crashedPreviously = true;
            numberOfCrashes++;
        }
    }

    public void reset() {
        scheduler.cancelAll();
        scheduler.unregisterAllSubsystems();
    }

    public Throwable getLastException() {
        return lastException;
    }

    public void cancelAll() {
        scheduler.cancelAll();
    }

    public void unregisterAllSubsystems() {
        scheduler.unregisterAllSubsystems();
    }

    public void registerSubsystem(Subsystem... subsystems) {
        scheduler.registerSubsystem(subsystems);
    }

    public void setDefaultCommand(Subsystem subsystem, Command defaultCommand) {
        scheduler.setDefaultCommand(subsystem, defaultCommand);
    }
}
