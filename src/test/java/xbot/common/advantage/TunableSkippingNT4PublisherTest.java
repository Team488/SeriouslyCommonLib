package xbot.common.advantage;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.littletonrobotics.junction.LogDataReceiver;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.LogTable.LogValue;

import xbot.common.properties.TunableManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TunableSkippingNT4PublisherTest {

    private CapturingReceiver wrapped;
    private TunableSkippingNT4Publisher publisher;

    @BeforeEach
    public void setUp() {
        wrapped = new CapturingReceiver();
        publisher = new TunableSkippingNT4Publisher(wrapped);
    }

    @Test
    public void nonTunableKeysAreForwarded() throws InterruptedException {
        LogTable input = new LogTable(123L);
        input.put("RealOutputs/Drive/Pose", 3.14);
        input.put("SystemStats/BatteryVoltage", 12.4);
        input.put("Timestamp", 123L);

        publisher.putTable(input);

        Map<String, LogValue> forwarded = wrapped.lastTable.getAll(false);
        assertNotNull(forwarded.get("/RealOutputs/Drive/Pose"));
        assertNotNull(forwarded.get("/SystemStats/BatteryVoltage"));
        assertNotNull(forwarded.get("/Timestamp"));
    }

    @Test
    public void tunableNamespacedKeysAreDropped() throws InterruptedException {
        LogTable input = new LogTable(1L);
        input.put(TunableManager.AKIT_LOG_NAMESPACE + "ShooterSubsystem/VoltageRampTime", 0.2);
        input.put(TunableManager.AKIT_LOG_NAMESPACE + "DriveSubsystem/MaxSpeed", 4.0);

        publisher.putTable(input);

        Map<String, LogValue> forwarded = wrapped.lastTable.getAll(false);
        assertNull(forwarded.get("/" + TunableManager.AKIT_LOG_NAMESPACE + "ShooterSubsystem/VoltageRampTime"));
        assertNull(forwarded.get("/" + TunableManager.AKIT_LOG_NAMESPACE + "DriveSubsystem/MaxSpeed"));
        assertTrue(forwarded.isEmpty());
    }

    @Test
    public void mixOfTunableAndNonTunableKeysIsHandled() throws InterruptedException {
        LogTable input = new LogTable(7L);
        input.put("RealOutputs/Shooter/RPM", 6000.0);
        input.put(TunableManager.AKIT_LOG_NAMESPACE + "Shooter/VoltageRampTime", 0.2);
        input.put("DriverStation/Alliance", "Red");
        input.put(TunableManager.AKIT_LOG_NAMESPACE + "Drive/MaxSpeed", 4.0);

        publisher.putTable(input);

        Map<String, LogValue> forwarded = wrapped.lastTable.getAll(false);
        assertNotNull(forwarded.get("/RealOutputs/Shooter/RPM"));
        assertNotNull(forwarded.get("/DriverStation/Alliance"));
        assertNull(forwarded.get("/" + TunableManager.AKIT_LOG_NAMESPACE + "Shooter/VoltageRampTime"));
        assertNull(forwarded.get("/" + TunableManager.AKIT_LOG_NAMESPACE + "Drive/MaxSpeed"));
        assertEquals(2, forwarded.size());
    }

    @Test
    public void timestampIsPreserved() throws InterruptedException {
        LogTable input = new LogTable(424242L);
        input.put("Timestamp", 424242L);

        publisher.putTable(input);

        assertEquals(424242L, wrapped.lastTable.getTimestamp());
    }

    @Test
    public void emptyInputDoesNotCrash() throws InterruptedException {
        LogTable input = new LogTable(0L);

        publisher.putTable(input);

        assertNotNull(wrapped.lastTable);
        assertTrue(wrapped.lastTable.getAll(false).isEmpty());
    }

    @Test
    public void startAndEndAreForwarded() {
        publisher.start();
        publisher.end();
        assertTrue(wrapped.started);
        assertTrue(wrapped.ended);
    }

    @Test
    public void denyPrefixIncludesLeadingSlash() {
        // Sanity check on the relationship between the namespace constant and the deny prefix.
        // LogTable.getAll() returns keys with a leading slash; the namespace doesn't have one.
        assertTrue(TunableSkippingNT4Publisher.DENY_PREFIX.startsWith("/"));
        assertEquals("/" + TunableManager.AKIT_LOG_NAMESPACE, TunableSkippingNT4Publisher.DENY_PREFIX);
    }

    @Test
    public void keyThatMerelyContainsTheNamespaceIsNotDropped() throws InterruptedException {
        // A key like /RealOutputs/TunableMirror/Foo happens to contain the namespace but
        // isn't under the namespace — it must still be forwarded.
        LogTable input = new LogTable(1L);
        input.put("RealOutputs/" + TunableManager.AKIT_LOG_NAMESPACE + "Foo", 1.0);

        publisher.putTable(input);

        Map<String, LogValue> forwarded = wrapped.lastTable.getAll(false);
        assertNotNull(forwarded.get("/RealOutputs/" + TunableManager.AKIT_LOG_NAMESPACE + "Foo"));
        assertFalse(forwarded.isEmpty());
    }

    private static final class CapturingReceiver implements LogDataReceiver {
        LogTable lastTable;
        boolean started;
        boolean ended;

        @Override
        public void start() {
            started = true;
        }

        @Override
        public void end() {
            ended = true;
        }

        @Override
        public void putTable(LogTable table) {
            lastTable = table;
        }
    }
}
