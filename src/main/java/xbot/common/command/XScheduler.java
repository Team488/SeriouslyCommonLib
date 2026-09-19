package xbot.common.command;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wpilib.command2.Command;
import org.wpilib.command2.CommandScheduler;
import org.wpilib.command2.Subsystem;
import org.wpilib.util.Alert;

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
        this.schedulerCrashedAlert = new Alert("SchedulerCrash", "Scheduler Crashed", Alert.Level.HIGH);
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
