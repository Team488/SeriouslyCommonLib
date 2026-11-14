package xbot.common.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.wpilib.command2.CommandScheduler;
import org.wpilib.util.AlertDataJNI;
import org.wpilib.util.AlertDataJNI.AlertInfo;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class XSchedulerTest extends BaseCommonLibTest {

    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @AfterEach
    public void tearDown() {
        XScheduler scheduler = getInjectorComponent().scheduler();
        scheduler.reset();
        scheduler.close();
    }

    @Test
    public void schedulerAlertsHaveIndependentIdentityAndLifecycle() {
        int initialSchedulerAlertCount = getSchedulerAlertCount();
        XScheduler firstScheduler = new XScheduler();
        XScheduler secondScheduler = new XScheduler();

        try {
            assertEquals(initialSchedulerAlertCount + 2, getSchedulerAlertCount());

            firstScheduler.schedulerCrashedAlert.setText("First scheduler crash");
            firstScheduler.schedulerCrashedAlert.set(true);

            assertEquals("First scheduler crash", firstScheduler.schedulerCrashedAlert.getText());
            assertEquals("Scheduler Crashed", secondScheduler.schedulerCrashedAlert.getText());
            assertTrue(firstScheduler.schedulerCrashedAlert.get());
            assertFalse(secondScheduler.schedulerCrashedAlert.get());
        } finally {
            firstScheduler.close();
            secondScheduler.close();
        }

        assertEquals(initialSchedulerAlertCount, getSchedulerAlertCount());
    }

    @Test
    public void testXSchedulerDoesntCrash() {
        BaseCommand crashingCommand = new CrashingOnInitCommand();
        CommandScheduler.getInstance().schedule(crashingCommand);
        XScheduler xScheduler = getInjectorComponent().scheduler();
        xScheduler.run();
        xScheduler.run();
        // shouldn't have crashed
    }

    @Test
    public void testXSchedulerDoesntCrashAndRecovers() {
        BaseCommand crashingCommand = new CrashingInExecCommand();
        CommandScheduler.getInstance().schedule(crashingCommand);
        XScheduler xScheduler = getInjectorComponent().scheduler();
        xScheduler.run();
        xScheduler.run();
        // shouldn't have crashed

        // scheduler should have been emptied.
    }

    @Test
    @Disabled("I can't make the scheduler crash - this needs more investigation later.")
    public void testSchedulerCrashes() {
        BaseCommand crashingCommand = new CrashingInExecCommand();
        CommandScheduler.getInstance().schedule(crashingCommand);

        boolean hitCrash = false;

        CommandScheduler.getInstance().run();

        try {
            // Note - the below call will never fully execute (and show up red on
            CommandScheduler.getInstance().run();
            CommandScheduler.getInstance().run();
            CommandScheduler.getInstance().run();
            CommandScheduler.getInstance().run();

        } catch (Exception e) {
            hitCrash = true;
        }

        assertTrue(hitCrash, "We should have crashed");
    }

    private static int getSchedulerAlertCount() {
        int count = 0;
        for (AlertInfo alert : AlertDataJNI.getAlerts()) {
            if ("SchedulerCrash".equals(alert.group)) {
                count++;
            }
        }
        return count;
    }

}

// CHECKSTYLE:OFF
class CrashingOnInitCommand extends BaseCommand {

    @Override
    public void initialize() {
        throw new RuntimeException();
    }

    @Override
    public void execute() {

    }
}

class CrashingInExecCommand extends BaseCommand {

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        throw new RuntimeException();
    }
}
// CHECKSTYLE:ON
