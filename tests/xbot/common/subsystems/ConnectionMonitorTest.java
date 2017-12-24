package xbot.common.subsystems;

import edu.wpi.first.wpilibj.MockTimer;
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
    public void testShortIntervalNoDisconnection() {
        BaseConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() / 4);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
    }

    @Test
    public void testLongIntervalNoDisconnection() {
        BaseConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() / 1.1);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
    }

    @Test
    public void testShortIntervalDisconnection() {
        BaseConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() * 1.1);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() > -1.0);
    }

    @Test
    public void testLongIntervalDisconnection() {
        BaseConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() * 500);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() > -1.0);
    }
}

@Singleton
class MockConnectionMonitor extends BaseConnectionMonitorSubsystem {

    @Inject
    public MockConnectionMonitor(XPropertyManager propertyManager) {
        super(propertyManager);
    }
}
