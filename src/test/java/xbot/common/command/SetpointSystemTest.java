package xbot.common.command;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.wpilib.command2.CommandScheduler;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    @Disabled
    public void testSetpointCommandsCollide() {
        XScheduler xScheduler = getInjectorComponent().scheduler();
        xScheduler.cancelAll();
        xScheduler.cancelAll();
        xScheduler.cancelAll();
        xScheduler.run();
        xScheduler.run();
        xScheduler.run();

        MockSetpointCommand first = getInjectorComponent().mockSetpointCommand();
        MockSetpointCommand second = getInjectorComponent().mockSetpointCommand();

        first.setRunsWhenDisabled(true);
        second.setRunsWhenDisabled(true);
        
        
        assertFalse(first.isScheduled(), "First command is not running");
        assertFalse(second.isScheduled(), "Second command is not running");
        
        CommandScheduler.getInstance().schedule(first);
        xScheduler.run();
        assertTrue(first.isScheduled(), "First command is running");
        
        CommandScheduler.getInstance().schedule(second);
        xScheduler.run();
        
        assertTrue(second.isScheduled(), "Second command is running");
        assertFalse(first.isScheduled(), "First command is no longer running");
    }
    
    
}
