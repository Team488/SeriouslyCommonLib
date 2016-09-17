package xbot.common.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.command.Scheduler;
import xbot.common.injection.BaseWPITest;

public class SetpointSystemTest extends BaseWPITest{

    @Before
    public void setUp() {
        super.setUp();
    }
    
    @Test
    public void testSetpointSystemCanBeCreated() {
        MockSetpointCommand first = injector.getInstance(MockSetpointCommand.class);
    }
    
    @Test
    public void testSetpointCommandsCollide() {
        MockSetpointCommand first = injector.getInstance(MockSetpointCommand.class);
        MockSetpointCommand second = injector.getInstance(MockSetpointCommand.class);
        
        XScheduler xScheduler = this.injector.getInstance(XScheduler.class);
        
        assertFalse("First command is not running", first.isRunning());
        assertFalse("Second command is not running", second.isRunning());
        
        Scheduler.getInstance().add(first);
        xScheduler.run();
        assertTrue("First command is running", first.isRunning());
        
        Scheduler.getInstance().add(second);
        xScheduler.run();
        
        assertTrue("Second command is running", second.isRunning());
        assertFalse("First command is no longer running", first.isRunning());
    }
    
    
}
