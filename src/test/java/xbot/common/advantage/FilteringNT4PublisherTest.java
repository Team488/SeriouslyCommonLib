package xbot.common.advantage;

import org.junit.Before;
import org.junit.Test;
import org.littletonrobotics.junction.LogDataReceiver;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.LogTable.LogValue;
import xbot.common.advantage.NetworkTablesPublishFilter.AllowlistStore;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class FilteringNT4PublisherTest {

    private NetworkTablesPublishFilter filter;
    private CapturingReceiver wrapped;
    private FilteringNT4Publisher publisher;

    @Before
    public void setUp() {
        filter = new NetworkTablesPublishFilter(new InMemoryStore());
        wrapped = new CapturingReceiver();
        publisher = new FilteringNT4Publisher(filter, wrapped);
    }

    private static final class InMemoryStore implements AllowlistStore {
        private String value = "";

        @Override
        public String load() {
            return value;
        }

        @Override
        public void save(String value) {
            this.value = value;
        }
    }

    @Test
    public void allowedKeysAreForwarded() throws InterruptedException {
        filter.setAllowedPrefixes(List.of("/RealOutputs/Drive"));

        LogTable input = new LogTable(12345L);
        input.put("RealOutputs/Drive/Pose", 3.14);
        input.put("RealOutputs/Shooter/RPM", 6000.0);

        publisher.putTable(input);

        Map<String, LogValue> forwarded = wrapped.lastTable.getAll(false);
        assertNotNull(forwarded.get("/RealOutputs/Drive/Pose"));
        assertNull(forwarded.get("/RealOutputs/Shooter/RPM"));
    }

    @Test
    public void alwaysOnKeysAreForwardedWithoutConfiguration() throws InterruptedException {
        LogTable input = new LogTable(99L);
        input.put("Timestamp", 99L);
        input.put("SystemStats/BatteryVoltage", 12.3);
        input.put("RealOutputs/SomethingElse", 1.0);

        publisher.putTable(input);

        Map<String, LogValue> forwarded = wrapped.lastTable.getAll(false);
        assertNotNull(forwarded.get("/Timestamp"));
        assertNotNull(forwarded.get("/SystemStats/BatteryVoltage"));
        assertNull(forwarded.get("/RealOutputs/SomethingElse"));
    }

    @Test
    public void seenKeysAreRecordedForAllInputs() throws InterruptedException {
        LogTable input = new LogTable(1L);
        input.put("Timestamp", 1L);
        input.put("RealOutputs/Drive/Pose", 0.0);
        input.put("RealOutputs/Shooter/RPM", 0.0);

        publisher.putTable(input);

        List<String> seen = filter.getSeenKeys();
        assertTrue(seen.contains("/Timestamp"));
        assertTrue(seen.contains("/RealOutputs/Drive/Pose"));
        assertTrue(seen.contains("/RealOutputs/Shooter/RPM"));
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
    public void filterChangesAreReflectedImmediately() throws InterruptedException {
        LogTable input = new LogTable(1L);
        input.put("RealOutputs/Drive/Pose", 1.0);

        publisher.putTable(input);
        assertFalse(wrapped.lastTable.getAll(false).containsKey("/RealOutputs/Drive/Pose"));

        filter.setAllowedPrefixes(List.of("RealOutputs/Drive"));
        publisher.putTable(input);
        assertTrue(wrapped.lastTable.getAll(false).containsKey("/RealOutputs/Drive/Pose"));
    }

    private static class CapturingReceiver implements LogDataReceiver {
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
