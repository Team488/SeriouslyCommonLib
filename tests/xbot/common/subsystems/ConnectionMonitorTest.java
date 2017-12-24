package xbot.common.subsystems;

import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.Timer;
import org.junit.Test;
import xbot.common.injection.BaseWPITest;
import xbot.common.properties.XPropertyManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import static junit.framework.TestCase.assertTrue;

public class ConnectionMonitorTest extends BaseWPITest {

    private MockTimer mockTimer;

    @Override
    public void setUp() {
        super.setUp();
        mockTimer = injector.getInstance(MockTimer.class);
        mockTimer.advanceTimeInSecondsBy(488);
    }

    @Test
    public void testConnectedDriverStation() {
        BaseConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        connectionMonitor.setLastPacketReceivedTimestamp(Timer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTime() == -1);
    }

    @Test
    public void testDisconnectedDriverStation() {
        BaseConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(488);
        connectionMonitor.setLastPacketReceivedTimestamp(Timer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTime() > 400);
    }
}

@Singleton
class MockConnectionMonitor extends BaseConnectionMonitorSubsystem {

    @Inject
    public MockConnectionMonitor(XPropertyManager propertyManager) {
        super(propertyManager);
    }
}
