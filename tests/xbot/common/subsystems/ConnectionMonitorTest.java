package xbot.common.subsystems;

import edu.wpi.first.wpilibj.MockTimer;
import edu.wpi.first.wpilibj.Timer;
import org.junit.Test;
import xbot.common.injection.BaseWPITest;

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
        connectionMonitor.setLastPacketReceivedTimeStamp(Timer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTime() == -1);
    }

    @Test
    public void testDisconnectedDriverStation() {
        BaseConnectionMonitorSubsystem connectionMonitor = injector.getInstance(MockConnectionMonitor.class);
        mockTimer.advanceTimeInSecondsBy(488);
        connectionMonitor.setLastPacketReceivedTimeStamp(Timer.getFPGATimestamp());
        assertTrue(connectionMonitor.getPreviousDisconnectionTime() > 400);
    }
}

@Singleton
class MockConnectionMonitor extends BaseConnectionMonitorSubsystem {

    public MockConnectionMonitor() {
        setTimeOut(1.0);
    }
}
