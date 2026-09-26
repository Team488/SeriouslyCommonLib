package xbot.common.command;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.wpilib.command2.CommandScheduler;
import org.wpilib.util.AlertDataJNI;
import org.wpilib.util.AlertDataJNI.AlertInfo;
import xbot.common.injection.BaseCommonLibTest;

public class XSchedulerTest extends BaseCommonLibTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
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
    @Ignore("I can't make the scheduler crash - this needs more investigation later.")
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

        assertTrue("We should have crashed", hitCrash);
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
