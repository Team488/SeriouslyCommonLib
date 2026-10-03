package xbot.common.command;

import java.lang.ref.Cleaner;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

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
public class XScheduler implements AutoCloseable {

    private static final Cleaner ALERT_CLEANER = Cleaner.create();
    private static final AtomicLong NEXT_ALERT_ID = new AtomicLong();
    private static Logger log = LogManager.getLogger(XScheduler.class);

    boolean crashedPreviously = false;

    int numberOfCrashes = 0;

    Throwable lastException = null;

    final Alert schedulerCrashedAlert;
    private final Cleaner.Cleanable schedulerCrashedAlertCleanup;
    final CommandScheduler scheduler;

    @Inject
    public XScheduler() {
        String alertId = getClass().getName() + "-" + NEXT_ALERT_ID.getAndIncrement();
        this.schedulerCrashedAlert = new Alert("SchedulerCrash", alertId, "Scheduler Crashed", Alert.Level.HIGH);
        this.schedulerCrashedAlertCleanup = ALERT_CLEANER.register(this, schedulerCrashedAlert::close);
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

    /**
     * Releases the native alert allocation when this scheduler wrapper will no longer be used.
     */
    @Override
    public void close() {
        schedulerCrashedAlertCleanup.clean();
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
