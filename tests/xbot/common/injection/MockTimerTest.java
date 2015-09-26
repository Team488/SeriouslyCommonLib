package xbot.common.injection;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;

import xbot.common.wpi_extensions.BaseCommand;
import xbot.common.wpi_extensions.XScheduler;
import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Scheduler;

public class MockTimerTest extends BaseWPITest {

	MockTimer timer;
	
	@Before
	public void setup() {
		timer = injector.getInstance(MockTimer.class);
	}
	
	@Test
	public void test_starts_zero() {
		assertEquals(0.0, Timer.getFPGATimestamp(), 0.001);
	}
	
	@Test
	public void updated_by_mock() {
		timer.setTimeInSeconds(10.0);
		assertEquals(10.0, Timer.getFPGATimestamp(), 0.001);
	}
	
    @Ignore
	public class TimeOut extends BaseCommand {
		@Override
		public void initialize() {
			this.setTimeout(10.0);
		}

		@Override
		public void execute() { }
		
		@Override
		public boolean isFinished() {
			return this.isTimedOut();
		}
	}
	
	@Test
	public void test_command_timed_out() {
		BaseCommand timeOut = new TimeOut();
		XScheduler xScheduler = this.injector.getInstance(XScheduler.class);
				
		Scheduler.getInstance().add(timeOut);
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
