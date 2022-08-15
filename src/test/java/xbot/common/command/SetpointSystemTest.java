package xbot.common.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import edu.wpi.first.wpilibj2.command.CommandScheduler;
import xbot.common.injection.BaseCommonLibTest;

public class SetpointSystemTest extends BaseCommonLibTest {

    @SuppressWarnings("unused")
    @Test
    public void testSetpointSystemCanBeCreated() {
        MockSetpointCommand first = getInjectorComponent().mockSetpointCommand();
    }
    
    // I think this test is failing because the robot isn't "enabled."
    // Yep, that's the issue. Setting these commands to RunWhenDisabled lets this test pass as
    // expected. Is there some way we can deal with this problem?
    // This is also impacted by other tests, somehow - it works in isolation
    // but fails when other tests are running.
    @Test
    @Ignore
    public void testSetpointCommandsCollide() {
        XScheduler xScheduler = getInjectorComponent().scheduler();
        xScheduler.removeAll();
        xScheduler.removeAll();
        xScheduler.removeAll();
        xScheduler.run();
        xScheduler.run();
        xScheduler.run();

        MockSetpointCommand first = getInjectorComponent().mockSetpointCommand();
        MockSetpointCommand second = getInjectorComponent().mockSetpointCommand();

        first.setRunsWhenDisabled(true);
        second.setRunsWhenDisabled(true);
        
        
        assertFalse("First command is not running", first.isScheduled());
        assertFalse("Second command is not running", second.isScheduled());
        
        CommandScheduler.getInstance().schedule(first);
        xScheduler.run();
        assertTrue("First command is running", first.isScheduled());
        
        CommandScheduler.getInstance().schedule(second);
        xScheduler.run();
        
        assertTrue("Second command is running", second.isScheduled());
        assertFalse("First command is no longer running", first.isScheduled());
    }
    
    
}
