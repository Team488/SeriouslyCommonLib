package xbot.common.command;

import java.lang.ref.Cleaner;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wpilib.command2.Command;
import org.wpilib.util.Alert;

import xbot.common.advantage.AKitLogger;
import xbot.common.logging.TimeLogger;
import xbot.common.properties.TunablePrefixProvider;

/**
 * Enhanced version of WPILib's Command that allows for extension of existing
 * functionality.
 */
public abstract class BaseCommand extends Command implements AutoCloseable, TunablePrefixProvider {

    private static final Cleaner ALERT_CLEANER = Cleaner.create();
    private static final AtomicLong NEXT_ALERT_ID = new AtomicLong();

    protected final Alert runningAlert;
    private final Cleaner.Cleanable runningAlertCleanup;
    protected final Logger log;
    protected final AKitLogger aKitLog;
    protected final TimeLogger monitor;
    private boolean configurableRunWhenDisabled;

    @Inject
    TunableCommandPublisher tunableCommandPublisher;

    public BaseCommand() {
        log = LogManager.getLogger(this.getName());
        aKitLog = new AKitLogger(this);
        monitor = new TimeLogger(this.getName(), 20);
        String alertId = getClass().getName() + "-" + NEXT_ALERT_ID.getAndIncrement();
        runningAlert = new Alert("Commands", alertId, this.getName(), Alert.Level.LOW);
        runningAlertCleanup = ALERT_CLEANER.register(this, runningAlert::close);
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
    public void initialize() {
        // the name might not be set at construction, so let's update it here
        this.runningAlert.setText(this.getName());
        this.runningAlert.set(true);
    }

    @Override
    public void end(boolean isInterrupted) {
        this.runningAlert.set(false);
    }

    /**
     * Releases the native alert allocation when this command will no longer be used.
     */
    @Override
    public void close() {
        runningAlertCleanup.clean();
    }

    public void publishToTunables() {
        if (tunableCommandPublisher != null) {
            tunableCommandPublisher.publish(this);
        }
    }

    public void publishToTunables(String label) {
        if (tunableCommandPublisher != null) {
            tunableCommandPublisher.publish(label, this);
        }
    }

}
