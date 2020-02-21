package xbot.common.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.injection.BaseWPITest;

public class BaseWaitForMaintainerCommandTest extends BaseWPITest {

    @Test
    public void testReachedGoal() {
        BaseSetpointSubsystem subsystem = injector.getInstance(MockSetpointSystem.class);
        BaseWaitForMaintainerCommand command = injector.getInstance(MockWaitForMaintainerCommand.class);

        command.initialize();

        subsystem.setMaintainerIsAtGoal(false);
        assertFalse(command.isFinished());

        subsystem.setMaintainerIsAtGoal(true);
        assertTrue(command.isFinished());
    }
    
    @Test
    public void testTimeout() {
        BaseSetpointSubsystem subsystem = injector.getInstance(MockSetpointSystem.class);
        BaseWaitForMaintainerCommand command = injector.getInstance(MockWaitForMaintainerCommand.class);
        MockTimer timer = injector.getInstance(MockTimer.class);

        command.initialize();

        subsystem.setMaintainerIsAtGoal(false);
        assertFalse(command.isFinished());

        timer.advanceTimeInSecondsBy(10);
        assertTrue(command.isFinished());
    }

}