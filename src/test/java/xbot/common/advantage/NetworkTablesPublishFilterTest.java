package xbot.common.advantage;

import org.junit.Test;
import xbot.common.advantage.NetworkTablesPublishFilter.AllowlistStore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NetworkTablesPublishFilterTest {

    /** Trivial in-memory {@link AllowlistStore} for tests. */
    private static final class InMemoryStore implements AllowlistStore {
        String value;

        InMemoryStore() {
            this("");
        }

        InMemoryStore(String initial) {
            this.value = initial;
        }

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
    public void alwaysOnPrefixesArePublishedWithoutConfiguration() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        assertTrue(filter.shouldPublish("/Timestamp"));
        assertTrue(filter.shouldPublish("/SystemStats/BatteryVoltage"));
        assertTrue(filter.shouldPublish("/DriverStation/Alliance"));
        assertTrue(filter.shouldPublish("/RealMetadata/GitSHA"));
        assertTrue(filter.shouldPublish("/ReplayMetadata/Source"));
    }

    @Test
    public void unconfiguredKeysAreNotPublished() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        assertFalse(filter.shouldPublish("/RealOutputs/DriveSubsystem/MaxSpeed"));
        assertFalse(filter.shouldPublish("/Drive/PID/P"));
    }

    @Test
    public void userPrefixesGateMatchingKeys() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.setAllowedPrefixes(List.of("/RealOutputs/DriveSubsystem"));

        assertTrue(filter.shouldPublish("/RealOutputs/DriveSubsystem/MaxSpeed"));
        assertTrue(filter.shouldPublish("/RealOutputs/DriveSubsystem/Pose"));
        assertFalse(filter.shouldPublish("/RealOutputs/Shooter/RPM"));
    }

    @Test
    public void prefixesAreNormalizedWithLeadingSlash() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.setAllowedPrefixes(List.of("Drive/", "  Shooter/PID  "));

        List<String> stored = filter.getAllowedPrefixes();
        assertEquals(2, stored.size());
        assertTrue(stored.contains("/Drive/"));
        assertTrue(stored.contains("/Shooter/PID"));
    }

    @Test
    public void emptyAndNullPrefixesAreSkipped() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.setAllowedPrefixes(Arrays.asList("Drive/", "", "   ", null, "Shooter/"));

        assertEquals(2, filter.getAllowedPrefixes().size());
    }

    @Test
    public void duplicatePrefixesAreCollapsed() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.setAllowedPrefixes(List.of("/Drive/", "Drive/", "/Drive/"));

        assertEquals(1, filter.getAllowedPrefixes().size());
    }

    @Test
    public void allowedPrefixesAreWrittenToStore() {
        InMemoryStore store = new InMemoryStore();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(store);
        filter.setAllowedPrefixes(List.of("Drive/", "Shooter/PID"));

        // Store value is the normalized, comma-separated, sorted list.
        assertEquals("/Drive/,/Shooter/PID", store.value);
    }

    @Test
    public void allowedPrefixesAreReloadedFromStore() {
        InMemoryStore store = new InMemoryStore("/Drive/,/Shooter/PID");
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(store);

        List<String> reloaded = filter.getAllowedPrefixes();
        assertEquals(2, reloaded.size());
        assertTrue(reloaded.contains("/Drive/"));
        assertTrue(reloaded.contains("/Shooter/PID"));
        assertTrue(filter.shouldPublish("/Drive/Pose"));
    }

    @Test
    public void missingStoreValueIsHandledGracefully() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore(""));
        assertTrue(filter.getAllowedPrefixes().isEmpty());
        assertTrue(filter.shouldPublish("/Timestamp"));
    }

    @Test
    public void whitespaceInStoreValueIsTolerated() {
        InMemoryStore store = new InMemoryStore("  Drive/ ,   Shooter/PID   ");
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(store);

        List<String> loaded = filter.getAllowedPrefixes();
        assertEquals(2, loaded.size());
        assertTrue(loaded.contains("/Drive/"));
        assertTrue(loaded.contains("/Shooter/PID"));
    }

    @Test
    public void recordingSeenKeysTracksUniqueValues() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.recordSeenKey("/Foo");
        filter.recordSeenKey("/Bar");
        filter.recordSeenKey("/Foo");
        filter.recordSeenKey(null);

        List<String> seen = filter.getSeenKeys();
        assertEquals(2, seen.size());
        assertTrue(seen.contains("/Foo"));
        assertTrue(seen.contains("/Bar"));
    }

    @Test
    public void seenKeysAreReturnedInSortedOrder() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.recordSeenKey("/Charlie");
        filter.recordSeenKey("/Alpha");
        filter.recordSeenKey("/Bravo");

        assertEquals(List.of("/Alpha", "/Bravo", "/Charlie"), filter.getSeenKeys());
    }

    @Test
    public void setAllowedPrefixesIsIdempotent() {
        TrackingStore store = new TrackingStore();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(store);
        filter.setAllowedPrefixes(List.of("Drive/"));
        int writesAfterFirst = store.writeCount;

        filter.setAllowedPrefixes(List.of("/Drive/")); // Same after normalization.
        assertEquals("Re-applying the same prefixes should not write the store again",
                writesAfterFirst, store.writeCount);
    }

    @Test
    public void differingPrefixesWriteToStore() {
        TrackingStore store = new TrackingStore();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(store);
        filter.setAllowedPrefixes(List.of("Drive/"));
        int writesAfterFirst = store.writeCount;

        filter.setAllowedPrefixes(List.of("Drive/", "Shooter/"));
        assertTrue("Adding a prefix should write the store again",
                store.writeCount > writesAfterFirst);
    }

    @Test
    public void emptyAllowlistOnlyPublishesAlwaysOnKeys() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.setAllowedPrefixes(Collections.emptyList());

        assertTrue(filter.shouldPublish("/Timestamp"));
        assertFalse(filter.shouldPublish("/RealOutputs/Anything"));
    }

    @Test
    public void prefixesMustMatchStart() {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(new InMemoryStore());
        filter.setAllowedPrefixes(List.of("/Drive"));

        assertTrue(filter.shouldPublish("/Drive/Pose"));
        assertFalse(filter.shouldPublish("/Shooter/Drive"));
    }

    @Test
    public void externalStoreEditsArePickedUpByPeriodic() {
        InMemoryStore store = new InMemoryStore();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(store);
        assertFalse(filter.shouldPublish("/Drive/Pose"));

        // Simulate a dashboard writing a new value into the store directly.
        store.value = "/Drive/";
        filter.periodic(0);

        assertTrue(filter.shouldPublish("/Drive/Pose"));
        assertTrue(filter.getAllowedPrefixes().contains("/Drive/"));
    }

    @Test
    public void periodicDoesNotReWriteStoreWhenNothingChanged() {
        TrackingStore store = new TrackingStore();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(store);
        filter.setAllowedPrefixes(List.of("Drive/"));
        int writesAfterSetup = store.writeCount;

        filter.periodic(0);
        filter.periodic(1000);

        assertEquals("Periodic should not touch the store when state matches",
                writesAfterSetup, store.writeCount);
    }

    /** Store that tracks how many times save() is called. */
    private static final class TrackingStore implements AllowlistStore {
        String value = "";
        int writeCount = 0;

        @Override
        public String load() {
            return value;
        }

        @Override
        public void save(String value) {
            this.value = value;
            writeCount++;
        }
    }
}
