package xbot.common.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import xbot.common.injection.BaseWPITest;

public class SetpointSystemTest extends BaseWPITest {

    @SuppressWarnings("unused")
    @Test
    public void testSetpointSystemCanBeCreated() {
        MockSetpointCommand first = injector.getInstance(MockSetpointCommand.class);
    }
    
    // I think this test is failing because the robot isn't "enabled."
    // Yep, that's the issue. Setting these commands to RunWhenDisabled lets this test pass as
    // expected. Is there some way we can deal with this problem?
    @Test
    public void testSetpointCommandsCollide() {
        MockSetpointCommand first = injector.getInstance(MockSetpointCommand.class);
        MockSetpointCommand second = injector.getInstance(MockSetpointCommand.class);

        first.setRunsWhenDisabled(true);
        second.setRunsWhenDisabled(true);
        
        XScheduler xScheduler = this.injector.getInstance(XScheduler.class);
        
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
