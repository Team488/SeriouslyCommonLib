package xbot.common.command;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import xbot.common.injection.BaseCommonLibTest;

public class XSchedulerTest extends BaseCommonLibTest {

    @Before
    public void setUp() {
        super.setUp();
    }

    @After
    public void tearDown() {
        getInjectorComponent().scheduler().removeAll();
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
