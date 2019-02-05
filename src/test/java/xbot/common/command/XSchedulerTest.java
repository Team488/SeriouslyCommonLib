package xbot.common.command;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.wpi.first.wpilibj.command.Scheduler;
import xbot.common.injection.BaseWPITest;

public class XSchedulerTest extends BaseWPITest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void tearDown() {
        this.injector.getInstance(XScheduler.class).removeAll();
    }

    @Test
    public void testXSchedulerDoesntCrash() {
        BaseCommand crashingCommand = new CrashingOnInitCommand();
        Scheduler.getInstance().add(crashingCommand);
        XScheduler xScheduler = this.injector.getInstance(XScheduler.class);
        xScheduler.run();
        xScheduler.run();
        // shouldn't have crashed
    }

    @Test
    public void testXSchedulerDoesntCrashAndRecovers() {
        BaseCommand crashingCommand = new CrashingInExecCommand();
        Scheduler.getInstance().add(crashingCommand);
        XScheduler xScheduler = this.injector.getInstance(XScheduler.class);
        xScheduler.run();
        xScheduler.run();
        // shouldn't have crashed

        // scheduler should have been emptied.
    }

    @Test 
    @Ignore("I can't make the scheduler crash - this needs more investigation later.")
    public void testSchedulerCrashes() {
        BaseCommand crashingCommand = new CrashingInExecCommand();
        Scheduler.getInstance().add(crashingCommand);

        boolean hitCrash = false;

        Scheduler.getInstance().run();

        try {
            // Note - the below call will never fully execute (and show up red on
            Scheduler.getInstance().run();
            Scheduler.getInstance().run();
            Scheduler.getInstance().run();
            Scheduler.getInstance().run();
            
        } catch (Exception e) {
            hitCrash = true;
        }

        assertTrue("We should have crashed", hitCrash);
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
