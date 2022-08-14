package xbot.common.injection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import xbot.common.command.XScheduler;

public class MockTimerTest extends BaseCommonLibTest {

    MockTimer timer;

    @Before
    public void setup() {
        timer = (MockTimer)getInjectorComponent().timerImplementation();
    }

    @Test
    public void test_starts_zero() {
        assertEquals(0.0, timer.getFPGATimestamp(), 0.001);
    }

    @Test
    public void updated_by_mock() {
        timer.setTimeInSeconds(10.0);
        assertEquals(10.0, timer.getFPGATimestamp(), 0.001);
    }
    
    @Test 
    @Ignore("Changes to WPI's Timer render this non-functional, until we find a way to inject depeer into their library")
    public void test_command_timed_out() {
        Command timeOut = new WaitCommand(5);
        XScheduler xScheduler = getInjectorComponent().scheduler();

        CommandScheduler.getInstance().schedule(timeOut);
        // Okay, so this is kind of weird. Basically, you need to call the scheduler twice for it
        // to actually pull information about the timer from the mock timer.
        xScheduler.run();
        xScheduler.run();
        // and currently, due to side effects from the Xscheduler tests, we need to run more times
        // to clear evil commands or something. (CrashOnInit command is still running)
        xScheduler.run();

        timer.setTimeInSeconds(11.0);
        assertTrue(timeOut.isFinished());
    }

}
