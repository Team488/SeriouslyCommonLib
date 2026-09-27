package xbot.common.command;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.wpilib.tunable.MockTunableBackend;
import org.wpilib.tunable.TunableRegistry;
import org.wpilib.tunable.Tunables;

import xbot.common.controls.sensors.XTimer;
import xbot.common.controls.sensors.mock_adapters.MockTimer;
import xbot.common.logging.RobotAssertionManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RealTunableCommandPublisherTest {

    private MockTunableBackend tunableBackend;

    @Before
    public void setUp() {
        TunableRegistry.reset();
        tunableBackend = new MockTunableBackend();
        TunableRegistry.registerBackend("", tunableBackend);
        XTimer.setImplementation(new MockTimer());
    }

    @After
    public void tearDown() {
        TunableRegistry.reset();
    }

    @Test
    public void publishUsesCommandNameAndReportsDuplicatePath() {
        String path = "RealTunableCommandPublisherTest/CommandName";
        MockCommand command = new MockCommand();
        command.setName(path);
        CapturingAssertionManager assertionManager = new CapturingAssertionManager();
        RealTunableCommandPublisher publisher = new RealTunableCommandPublisher(assertionManager);

        try {
            publisher.publish(command);
            publisher.publish(path, command);

            assertEquals(1, assertionManager.failureCount);
            assertNotNull(assertionManager.failureMessage);
            assertTrue(assertionManager.failureMessage.contains(path));
            assertTrue(assertionManager.failureMessage.contains("the first command remains published"));
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
        CapturingAssertionManager assertionManager = new CapturingAssertionManager();
        RealTunableCommandPublisher publisher = new RealTunableCommandPublisher(assertionManager);

        try {
            publisher.publish(path, firstCommand);
            publisher.publish(path, duplicateCommand);

            assertEquals("FirstCommand", tunableBackend.getValue(path + "/name", String.class));
            assertEquals(1, assertionManager.failureCount);
            assertNotNull(assertionManager.failureMessage);
            assertTrue(assertionManager.failureMessage.contains(path));
            assertTrue(assertionManager.failureMessage.contains("the first command remains published"));
        } finally {
            Tunables.remove(path);
            firstCommand.close();
            duplicateCommand.close();
        }
    }

    private static class CapturingAssertionManager extends RobotAssertionManager {
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
