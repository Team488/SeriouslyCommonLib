package xbot.common.command;

import org.junit.Test;

import org.wpilib.command2.Command;
import org.wpilib.command2.CommandScheduler;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

public class BaseParallelCommandGroupTest extends BaseCommonLibTest {

    @Test
    public void defaultsToCancelIncoming() {
        BaseParallelCommandGroup group = new BaseParallelCommandGroup();

        try {
            assertEquals(Command.InterruptionBehavior.CANCEL_INCOMING, group.getInterruptionBehavior());
        } finally {
            group.close();
        }
    }

    @Test
    public void aggregatesCancelSelfFromChildCommand() {
        MockCommand child = new MockCommand() {
            @Override
            public InterruptionBehavior getInterruptionBehavior() {
                return InterruptionBehavior.CANCEL_SELF;
            }
        };
        BaseParallelCommandGroup group = new BaseParallelCommandGroup(child);

        try {
            assertEquals(Command.InterruptionBehavior.CANCEL_SELF, group.getInterruptionBehavior());
        } finally {
            CommandScheduler.getInstance().removeComposedCommand(child);
            group.close();
            child.close();
        }
    }
}
