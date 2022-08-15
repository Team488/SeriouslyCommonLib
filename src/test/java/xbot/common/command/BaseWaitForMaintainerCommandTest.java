package xbot.common.command;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xbot.common.injection.BaseCommonLibTest;

public class BaseWaitForMaintainerCommandTest extends BaseCommonLibTest {

    @Test
    public void testReachedGoal() {
        BaseSetpointSubsystem subsystem = getInjectorComponent().mockSetpointSubsystem();
        BaseWaitForMaintainerCommand command = getInjectorComponent().mockWaitForMaintainerCommand();

        command.initialize();

        subsystem.setMaintainerIsAtGoal(false);
        assertFalse(command.isFinished());

        subsystem.setMaintainerIsAtGoal(true);
        assertTrue(command.isFinished());
    }
    
    @Test
    public void testTimeout() {
        BaseSetpointSubsystem subsystem = getInjectorComponent().mockSetpointSubsystem();
        BaseWaitForMaintainerCommand command = getInjectorComponent().mockWaitForMaintainerCommand();

        command.initialize();

        subsystem.setMaintainerIsAtGoal(false);
        assertFalse(command.isFinished());

        this.timer.advanceTimeInSecondsBy(10);
        assertTrue(command.isFinished());
    }

}