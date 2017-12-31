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
    public void testShortIntervalNoDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() / 4.0);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
    }

    @Test
    public void testLongIntervalNoDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() / 1.1);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
    }

    @Test
    public void testShortIntervalDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() * 1.1);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() > -1.0);
    }

    @Test
    public void testLongIntervalDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        connectionMonitor.setLastPacketReceivedTimestamp(Timer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() * 500.0);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() > -1.0);
    }

    @Test
    public void testMultipleSetPacketCallsWithNoDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        for (int i = 0; i < 488; i++) {
            mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() - (connectionMonitor.timeOut.get() / 2.0));
            connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        }
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
    }

    @Test
    public void testMultipleSetPacketCallsWithDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        for (int i = 0; i < 488; i++) {
            if (i == 244) {
                mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() * 2.0);
            }
            mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() - (connectionMonitor.timeOut.get() / 2.0));
            connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        }
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() >= -1.0);
    }

    @Test
    public void testVaryingTimeOutWithNoDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() / 4.0);
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
        connectionMonitor.timeOut.set(500);
        assertTrue(connectionMonitor.timeOut.get() >= 500);
        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() / 2.0);
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);
    }

    @Test
    public void testVaryingTimeOutWithDisconnection() {
        ConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        connectionMonitor.setLastPacketReceivedTimestamp(Timer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTimestamp() <= -1.0);

        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() * 2);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        double previousVal = connectionMonitor.getPreviousDisconnectionTimestamp();
        assertTrue(previousVal > -1.0);

        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() / 4.0);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        connectionMonitor.timeOut.set(500);
        assertTrue(connectionMonitor.timeOut.get() >= 500);

        mockTimer.advanceTimeInSecondsBy(connectionMonitor.timeOut.get() * 2.0);
        connectionMonitor.setLastPacketReceivedTimestamp(mockTimer.getFPGATimestamp());
        double currentVal = connectionMonitor.getPreviousDisconnectionTimestamp();
        assertTrue(currentVal > -1.0 && currentVal != previousVal);
    }
}

@Singleton
class MockConnectionMonitor extends ConnectionMonitorSubsystem {

    @Inject
    public MockConnectionMonitor(XPropertyManager propertyManager) {
        super(propertyManager);
    }
}
