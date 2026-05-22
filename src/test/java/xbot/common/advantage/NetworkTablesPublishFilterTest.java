package xbot.common.advantage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class NetworkTablesPublishFilterTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private Path filterFile() throws IOException {
        return tempFolder.newFolder().toPath().resolve("filter.json");
    }

    @Test
    public void alwaysOnPrefixesArePublishedWithoutConfiguration() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        assertTrue(filter.shouldPublish("/Timestamp"));
        assertTrue(filter.shouldPublish("/SystemStats/BatteryVoltage"));
        assertTrue(filter.shouldPublish("/DriverStation/Alliance"));
        assertTrue(filter.shouldPublish("/RealMetadata/GitSHA"));
        assertTrue(filter.shouldPublish("/ReplayMetadata/Source"));
    }

    @Test
    public void unconfiguredKeysAreNotPublished() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        assertFalse(filter.shouldPublish("/RealOutputs/DriveSubsystem/MaxSpeed"));
        assertFalse(filter.shouldPublish("/Drive/PID/P"));
    }

    @Test
    public void userPrefixesGateMatchingKeys() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        filter.setAllowedPrefixes(List.of("/RealOutputs/DriveSubsystem"));

        assertTrue(filter.shouldPublish("/RealOutputs/DriveSubsystem/MaxSpeed"));
        assertTrue(filter.shouldPublish("/RealOutputs/DriveSubsystem/Pose"));
        assertFalse(filter.shouldPublish("/RealOutputs/Shooter/RPM"));
    }

    @Test
    public void prefixesAreNormalizedWithLeadingSlash() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        filter.setAllowedPrefixes(List.of("Drive/", "  Shooter/PID  "));

        List<String> stored = filter.getAllowedPrefixes();
        assertEquals(2, stored.size());
        assertTrue(stored.contains("/Drive/"));
        assertTrue(stored.contains("/Shooter/PID"));
    }

    @Test
    public void emptyAndNullPrefixesAreSkipped() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        filter.setAllowedPrefixes(Arrays.asList("Drive/", "", "   ", null, "Shooter/"));

        assertEquals(2, filter.getAllowedPrefixes().size());
    }

    @Test
    public void duplicatePrefixesAreCollapsed() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        filter.setAllowedPrefixes(List.of("/Drive/", "Drive/", "/Drive/"));

        assertEquals(1, filter.getAllowedPrefixes().size());
    }

    @Test
    public void allowedPrefixesPersistAcrossInstances() throws IOException {
        Path file = filterFile();

        NetworkTablesPublishFilter first = new NetworkTablesPublishFilter(file);
        first.setAllowedPrefixes(List.of("Drive/", "Shooter/PID"));
        first.flushToDisk();

        NetworkTablesPublishFilter second = new NetworkTablesPublishFilter(file);
        List<String> reloaded = second.getAllowedPrefixes();
        assertEquals(2, reloaded.size());
        assertTrue(reloaded.contains("/Drive/"));
        assertTrue(reloaded.contains("/Shooter/PID"));
        assertTrue(second.shouldPublish("/Drive/Pose"));
    }

    @Test
    public void missingPersistenceFileIsHandledGracefully() throws IOException {
        Path file = tempFolder.newFolder().toPath().resolve("does-not-exist.json");
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(file);
        assertTrue(filter.getAllowedPrefixes().isEmpty());
    }

    @Test
    public void malformedPersistenceFileIsHandledGracefully() throws IOException {
        Path file = filterFile();
        Files.writeString(file, "{not valid json");

        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(file);
        assertTrue(filter.getAllowedPrefixes().isEmpty());
        assertTrue(filter.shouldPublish("/Timestamp"));
    }

    @Test
    public void persistenceFileMissingFieldIsHandledGracefully() throws IOException {
        Path file = filterFile();
        Files.writeString(file, "{}");

        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(file);
        assertTrue(filter.getAllowedPrefixes().isEmpty());
    }

    @Test
    public void recordingSeenKeysTracksUniqueValues() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
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
    public void seenKeysAreReturnedInSortedOrder() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        filter.recordSeenKey("/Charlie");
        filter.recordSeenKey("/Alpha");
        filter.recordSeenKey("/Bravo");

        assertEquals(List.of("/Alpha", "/Bravo", "/Charlie"), filter.getSeenKeys());
    }

    @Test
    public void setAllowedPrefixesIsIdempotent() throws IOException {
        Path file = filterFile();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(file);
        filter.setAllowedPrefixes(List.of("Drive/"));
        filter.flushToDisk();
        long firstModified = Files.getLastModifiedTime(file).toMillis();

        // Wait long enough to detect mtime change if a write happened.
        sleepQuietly(50);

        filter.setAllowedPrefixes(List.of("/Drive/"));
        filter.flushToDisk();
        long secondModified = Files.getLastModifiedTime(file).toMillis();

        assertEquals("Re-applying the same prefixes should not rewrite the file",
                firstModified, secondModified);
    }

    @Test
    public void differingPrefixesScheduleAFileWrite() throws IOException {
        Path file = filterFile();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(file);
        filter.setAllowedPrefixes(List.of("Drive/"));
        filter.flushToDisk();
        long firstModified = Files.getLastModifiedTime(file).toMillis();

        sleepQuietly(50);

        filter.setAllowedPrefixes(List.of("Drive/", "Shooter/"));
        filter.flushToDisk();
        long secondModified = Files.getLastModifiedTime(file).toMillis();

        assertNotEquals(firstModified, secondModified);
    }

    @Test
    public void saveToDiskProducesReadableJson() throws IOException {
        Path file = filterFile();
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(file);
        filter.setAllowedPrefixes(List.of("Drive/", "Shooter/PID"));
        filter.flushToDisk();

        JSONObject json = new JSONObject(Files.readString(file));
        JSONArray arr = json.getJSONArray("allowedPrefixes");
        assertEquals(2, arr.length());
    }

    @Test
    public void emptyAllowlistOnlyPublishesAlwaysOnKeys() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        filter.setAllowedPrefixes(Collections.emptyList());

        assertTrue(filter.shouldPublish("/Timestamp"));
        assertFalse(filter.shouldPublish("/RealOutputs/Anything"));
    }

    @Test
    public void prefixesMustMatchStart() throws IOException {
        NetworkTablesPublishFilter filter = new NetworkTablesPublishFilter(filterFile());
        filter.setAllowedPrefixes(List.of("/Drive"));

        assertTrue(filter.shouldPublish("/Drive/Pose"));
        assertFalse(filter.shouldPublish("/Shooter/Drive"));
    }

    private static void sleepQuietly(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
