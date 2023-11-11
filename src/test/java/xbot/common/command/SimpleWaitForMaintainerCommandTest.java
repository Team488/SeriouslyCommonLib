package xbot.common.command;

import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SimpleWaitForMaintainerCommandTest extends BaseCommonLibTest {
    @Test
    public void testTimeout() {
        var subsystem = new MockSetpointSubsystem();
        var command = new SimpleWaitForMaintainerCommand(subsystem, () -> 1.0);
        subsystem.setCurrentValue(1.0);
        assertFalse(subsystem.isMaintainerAtGoal());

        command.initialize();
        command.execute();
        assertFalse(command.isFinished());

        this.timer.advanceTimeInSecondsBy(1.5);
        command.execute();
        assertTrue(command.isFinished());
    }

    @Test
    public void testMaintainerAtGoal() {
        var subsystem = new MockSetpointSubsystem();
        var command = new SimpleWaitForMaintainerCommand(subsystem, () -> 1.0);
        subsystem.setCurrentValue(1.0);
        assertFalse(subsystem.isMaintainerAtGoal());

        command.initialize();
        command.execute();
        assertFalse(command.isFinished());

        subsystem.setCurrentValue(0.0);
        subsystem.setMaintainerIsAtGoal(true);
        this.timer.advanceTimeInSecondsBy(0.2);
        command.execute();
        assertTrue(command.isFinished());
    }
}
