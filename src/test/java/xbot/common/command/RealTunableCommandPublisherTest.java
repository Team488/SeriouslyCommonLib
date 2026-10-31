package xbot.common.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.wpilib.tunable.MockTunableBackend;
import org.wpilib.tunable.TunableRegistry;
import org.wpilib.tunable.Tunables;

import xbot.common.controls.sensors.XTimer;
import xbot.common.controls.sensors.mock_adapters.MockTimer;
import xbot.common.logging.assertion.RobotAssertionHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RealTunableCommandPublisherTest {

    private MockTunableBackend tunableBackend;

    @BeforeEach
    public void setUp() {
        TunableRegistry.reset();
        tunableBackend = new MockTunableBackend();
        TunableRegistry.registerBackend("", tunableBackend);
        XTimer.setImplementation(new MockTimer());
    }

    @AfterEach
    public void tearDown() {
        TunableRegistry.reset();
    }

    @Test
    public void publishUsesCommandNameAndReportsDuplicatePath() {
        String path = "RealTunableCommandPublisherTest/CommandName";
        MockCommand command = new MockCommand();
        command.setName(path);
        CapturingAssertionHandler assertionHandler = new CapturingAssertionHandler();
        RealTunableCommandPublisher publisher = new RealTunableCommandPublisher(assertionHandler);

        try {
            publisher.publish(command);
            publisher.publish(path, command);

            assertEquals(1, assertionHandler.failureCount);
            assertNotNull(assertionHandler.failureMessage);
            assertTrue(assertionHandler.failureMessage.contains(path));
            assertTrue(assertionHandler.failureMessage.contains("the first command remains published"));
        } finally {
            Tunables.remove(path);
            command.close();
        }
    }

    @Test
    public void publishUsesProvidedLabelAndReportsDuplicatePath() {
        String path = "RealTunableCommandPublisherTest/Label";
        MockCommand firstCommand = new MockCommand();
        firstCommand.setName("FirstCommand");
        MockCommand duplicateCommand = new MockCommand();
        duplicateCommand.setName("DuplicateCommand");
        CapturingAssertionHandler assertionHandler = new CapturingAssertionHandler();
        RealTunableCommandPublisher publisher = new RealTunableCommandPublisher(assertionHandler);

        try {
            publisher.publish(path, firstCommand);
            publisher.publish(path, duplicateCommand);

            assertEquals("FirstCommand", tunableBackend.getValue(path + "/name", String.class));
            assertEquals(1, assertionHandler.failureCount);
            assertNotNull(assertionHandler.failureMessage);
            assertTrue(assertionHandler.failureMessage.contains(path));
            assertTrue(assertionHandler.failureMessage.contains("the first command remains published"));
        } finally {
            Tunables.remove(path);
            firstCommand.close();
            duplicateCommand.close();
        }
    }

    private static class CapturingAssertionHandler extends RobotAssertionHandler {
        private String failureMessage;
        private int failureCount;

        @Override
        protected void handlePlatformException(RuntimeException exception) {
            failureCount++;
            failureMessage = exception.getMessage();
        }

        @Override
        public boolean isExceptionsEnabled() {
            return false;
        }
    }
}
