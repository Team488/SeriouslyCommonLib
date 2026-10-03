package xbot.common.command;

import org.junit.Test;

import org.wpilib.util.AlertDataJNI;
import org.wpilib.util.AlertDataJNI.AlertInfo;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class BaseCommandTest extends BaseCommonLibTest {

    @Test
    public void testPublishingToTunablesUsesCommandNameAndProvidedLabel() {
        BaseCommand command = new MockCommand();
        CapturingTunableCommandPublisher publisher = new CapturingTunableCommandPublisher();
        command.tunableCommandPublisher = publisher;
        command.setName("CommandName");

        try {
            command.publishToTunables();
            assertEquals("CommandName", publisher.path);
            assertSame(command, publisher.command);

            command.publishToTunables("label");
            assertEquals("label", publisher.path);
            assertSame(command, publisher.command);
        } finally {
            command.close();
        }
    }

    @Test
    public void runningAlertsHaveIndependentIdentityAndLifecycle() {
        int initialCommandAlertCount = getCommandAlertCount();
        BaseCommand firstCommand = new BaseCommand() {};
        BaseCommand secondCommand = new BaseCommand() {};

        try {
            assertEquals(initialCommandAlertCount + 2, getCommandAlertCount());

            firstCommand.setName("First command");
            secondCommand.setName("Second command");
            firstCommand.initialize();
            secondCommand.initialize();

            assertEquals("First command", firstCommand.runningAlert.getText());
            assertEquals("Second command", secondCommand.runningAlert.getText());
            assertTrue(firstCommand.runningAlert.get());
            assertTrue(secondCommand.runningAlert.get());

            firstCommand.end(false);

            assertFalse(firstCommand.runningAlert.get());
            assertTrue(secondCommand.runningAlert.get());
        } finally {
            firstCommand.close();
            secondCommand.close();
        }

        assertEquals(initialCommandAlertCount, getCommandAlertCount());
    }

    private static int getCommandAlertCount() {
        int count = 0;
        for (AlertInfo alert : AlertDataJNI.getAlerts()) {
            if ("Commands".equals(alert.group)) {
                count++;
            }
        }
        return count;
    }

    private static class CapturingTunableCommandPublisher implements TunableCommandPublisher {
        private String path;
        private BaseCommand command;

        @Override
        public void publish(BaseCommand command) {
            path = command.getName();
            this.command = command;
        }

        @Override
        public void publish(String label, BaseCommand command) {
            path = label;
            this.command = command;
        }
    }
}
