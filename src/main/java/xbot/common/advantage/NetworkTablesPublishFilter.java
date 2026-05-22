package xbot.common.advantage;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.networktables.StringArraySubscriber;
import edu.wpi.first.networktables.StringArrayTopic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Controls which AdvantageKit log-table keys are published to NetworkTables.
 *
 * <p>The filter holds a list of allowed prefixes (matched against keys produced by AdvantageKit's
 * {@code LogTable}). A small built-in "always-on" set ensures essentials like the timestamp,
 * driver-station data, and system stats keep flowing regardless of user configuration.
 *
 * <p>The rule list is persisted to a JSON file on disk so it survives reboots, and mirrored to
 * NetworkTables under {@code Tuning/AllowedPrefixes} so it can be edited live from a dashboard.
 * A running catalog of every key the system has ever seen is published to {@code Tuning/AvailableKeys}
 * so operators can discover what's available to allowlist.
 *
 * <p>The class itself is decoupled from NetworkTables for testability — call
 * {@link #connectToNetworkTables()} once NT is ready, then call {@link #periodic()} every loop.
 */
public class NetworkTablesPublishFilter {
    private static final Logger log = LogManager.getLogger(NetworkTablesPublishFilter.class);

    static final String NT_TABLE = "Tuning";
    static final String NT_ALLOWED_PREFIXES = "AllowedPrefixes";
    static final String NT_AVAILABLE_KEYS = "AvailableKeys";

    static final long CATALOG_PUBLISH_INTERVAL_MS = 1000;
    static final long FILE_WRITE_DEBOUNCE_MS = 1000;

    private static final List<String> ALWAYS_ON_PREFIXES = List.of(
            "/Timestamp",
            "/SystemStats",
            "/DriverStation",
            "/RealMetadata",
            "/ReplayMetadata"
    );

    private static final String JSON_KEY_ALLOWED_PREFIXES = "allowedPrefixes";

    private final Path persistenceFile;

    private volatile List<String> allowedPrefixes = List.of();
    private final ConcurrentSkipListSet<String> seenKeys = new ConcurrentSkipListSet<>();

    private StringArrayPublisher allowedPrefixesPublisher;
    private StringArraySubscriber allowedPrefixesSubscriber;
    private StringArrayPublisher availableKeysPublisher;

    private long lastCatalogPublishMs = 0;
    private int lastCatalogSize = -1;
    private long lastFileWriteMs = 0;
    private boolean pendingFileWrite = false;

    public NetworkTablesPublishFilter(Path persistenceFile) {
        this.persistenceFile = persistenceFile;
        loadFromDisk();
    }

    /**
     * Picks the appropriate persistence file location based on the runtime environment.
     * Prefers a USB stick mount, falls back to the roboRIO home directory, and finally
     * to the working directory (for desktop/sim).
     */
    public static Path defaultPersistenceFile() {
        File usb = new File("/U");
        if (usb.exists() && usb.isDirectory() && usb.canWrite()) {
            return new File(usb, "tuning-filter.json").toPath();
        }
        File rioHome = new File("/home/lvuser");
        if (rioHome.exists() && rioHome.isDirectory() && rioHome.canWrite()) {
            return new File(rioHome, "tuning-filter.json").toPath();
        }
        return new File(System.getProperty("user.dir"), "tuning-filter.json").toPath();
    }

    /**
     * @return The currently allowed user-specified prefixes (does not include the always-on set).
     */
    public List<String> getAllowedPrefixes() {
        return new ArrayList<>(allowedPrefixes);
    }

    /**
     * Replace the user-specified allowed prefixes with the given list. Mirrors the new value to
     * NetworkTables (if connected) and schedules a debounced write to disk.
     *
     * <p>Prefixes are normalized to always start with a leading {@code /} so they match the keys
     * produced by AdvantageKit's {@code LogTable.getAll()}.
     */
    public synchronized void setAllowedPrefixes(List<String> prefixes) {
        List<String> normalized = normalize(prefixes);
        if (normalized.equals(this.allowedPrefixes)) {
            return;
        }
        this.allowedPrefixes = normalized;
        pendingFileWrite = true;
        if (allowedPrefixesPublisher != null) {
            allowedPrefixesPublisher.set(normalized.toArray(new String[0]));
        }
    }

    /**
     * @return True if the given log-table key should be forwarded to NetworkTables.
     */
    public boolean shouldPublish(String key) {
        if (key == null) {
            return false;
        }
        for (String prefix : ALWAYS_ON_PREFIXES) {
            if (key.startsWith(prefix)) {
                return true;
            }
        }
        List<String> snapshot = allowedPrefixes;
        for (String prefix : snapshot) {
            if (key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Records that a key was seen flowing through the publisher. The full set is published
     * periodically to NetworkTables so dashboards can discover available paths.
     */
    public void recordSeenKey(String key) {
        if (key != null) {
            seenKeys.add(key);
        }
    }

    /**
     * @return A sorted snapshot of every key seen by the publisher so far.
     */
    public List<String> getSeenKeys() {
        return new ArrayList<>(seenKeys);
    }

    /**
     * Connects to NetworkTables and starts mirroring the filter state. Must be called once
     * before {@link #periodic()} will publish or react to NT updates.
     */
    public synchronized void connectToNetworkTables() {
        connectToNetworkTables(NetworkTableInstance.getDefault());
    }

    synchronized void connectToNetworkTables(NetworkTableInstance instance) {
        NetworkTable table = instance.getTable(NT_TABLE);
        StringArrayTopic prefsTopic = table.getStringArrayTopic(NT_ALLOWED_PREFIXES);
        allowedPrefixesPublisher = prefsTopic.publish();
        allowedPrefixesSubscriber = prefsTopic.subscribe(allowedPrefixes.toArray(new String[0]));
        availableKeysPublisher = table.getStringArrayTopic(NT_AVAILABLE_KEYS).publish();
        allowedPrefixesPublisher.set(allowedPrefixes.toArray(new String[0]));
    }

    /**
     * Call once per robot loop. Polls NetworkTables for filter edits, publishes the
     * available-keys catalog (debounced), and flushes the rule list to disk (debounced).
     */
    public synchronized void periodic() {
        periodic(System.currentTimeMillis());
    }

    synchronized void periodic(long nowMs) {
        if (allowedPrefixesSubscriber != null) {
            String[] ntValue = allowedPrefixesSubscriber.get();
            if (ntValue != null
                    && !Arrays.equals(ntValue, allowedPrefixes.toArray(new String[0]))) {
                setAllowedPrefixes(Arrays.asList(ntValue));
            }
        }

        if (availableKeysPublisher != null
                && nowMs - lastCatalogPublishMs >= CATALOG_PUBLISH_INTERVAL_MS
                && seenKeys.size() != lastCatalogSize) {
            String[] snapshot = seenKeys.toArray(new String[0]);
            availableKeysPublisher.set(snapshot);
            lastCatalogPublishMs = nowMs;
            lastCatalogSize = snapshot.length;
        }

        if (pendingFileWrite && nowMs - lastFileWriteMs >= FILE_WRITE_DEBOUNCE_MS) {
            saveToDisk();
            pendingFileWrite = false;
            lastFileWriteMs = nowMs;
        }
    }

    /**
     * Forces an immediate write of pending state to disk. Intended for shutdown hooks
     * and tests.
     */
    public synchronized void flushToDisk() {
        if (pendingFileWrite) {
            saveToDisk();
            pendingFileWrite = false;
            lastFileWriteMs = System.currentTimeMillis();
        }
    }

    private List<String> normalize(List<String> prefixes) {
        if (prefixes == null) {
            return List.of();
        }
        List<String> result = new ArrayList<>(prefixes.size());
        for (String p : prefixes) {
            if (p == null) {
                continue;
            }
            String trimmed = p.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (!trimmed.startsWith("/")) {
                trimmed = "/" + trimmed;
            }
            if (!result.contains(trimmed)) {
                result.add(trimmed);
            }
        }
        Collections.sort(result);
        return List.copyOf(result);
    }

    private void loadFromDisk() {
        if (!Files.exists(persistenceFile)) {
            log.info("No tuning filter file at {}; starting with empty allowlist.", persistenceFile);
            return;
        }
        try {
            String content = Files.readString(persistenceFile);
            JSONObject json = new JSONObject(content);
            JSONArray arr = json.optJSONArray(JSON_KEY_ALLOWED_PREFIXES);
            if (arr == null) {
                log.warn("Tuning filter file at {} did not contain '{}'; starting with empty allowlist.",
                        persistenceFile, JSON_KEY_ALLOWED_PREFIXES);
                return;
            }
            List<String> loaded = new ArrayList<>(arr.length());
            for (int i = 0; i < arr.length(); i++) {
                loaded.add(arr.getString(i));
            }
            this.allowedPrefixes = normalize(loaded);
            log.info("Loaded {} allowed NT prefix(es) from {}.", this.allowedPrefixes.size(), persistenceFile);
        } catch (IOException e) {
            log.error("Failed to read tuning filter file at " + persistenceFile, e);
        } catch (RuntimeException e) {
            log.error("Failed to parse tuning filter file at " + persistenceFile, e);
        }
    }

    private void saveToDisk() {
        try {
            JSONObject json = new JSONObject();
            json.put(JSON_KEY_ALLOWED_PREFIXES, new JSONArray(allowedPrefixes));
            Path parent = persistenceFile.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
            Files.writeString(persistenceFile, json.toString(2));
            log.debug("Wrote {} allowed NT prefix(es) to {}.", allowedPrefixes.size(), persistenceFile);
        } catch (IOException e) {
            log.error("Failed to write tuning filter file at " + persistenceFile, e);
        }
    }
}
