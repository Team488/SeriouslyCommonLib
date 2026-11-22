package xbot.common.command;

import org.junit.jupiter.api.Test;

import org.wpilib.simulation.SimHooks;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        SimHooks.stepTiming(1.5);
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
        SimHooks.stepTiming(0.2);
        command.execute();
        assertTrue(command.isFinished());
    }
}
