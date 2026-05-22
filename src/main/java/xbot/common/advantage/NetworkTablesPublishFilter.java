package xbot.common.advantage;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.wpilibj.Preferences;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
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
 * <p>The rule list is persisted via WPILib's {@code Preferences} system, so it survives reboots
 * and is mirrored to NetworkTables under {@code /Preferences/Tuning/AllowedPrefixes} where any
 * dashboard can edit it live. The value is stored as a comma-separated string. A running catalog
 * of every key seen flows to {@code /Tuning/AvailableKeys} so operators can discover what's
 * available to allowlist.
 *
 * <p>Persistence is decoupled from {@link Preferences} via {@link AllowlistStore} so tests can
 * exercise the filter without a live NetworkTables instance.
 */
public class NetworkTablesPublishFilter {
    private static final Logger log = LogManager.getLogger(NetworkTablesPublishFilter.class);

    /** Preferences key used by the default production store. */
    public static final String PREFERENCES_KEY = "Tuning/AllowedPrefixes";

    static final String NT_TABLE = "Tuning";
    static final String NT_AVAILABLE_KEYS = "AvailableKeys";

    static final long CATALOG_PUBLISH_INTERVAL_MS = 1000;

    private static final List<String> ALWAYS_ON_PREFIXES = List.of(
            "/Timestamp",
            "/SystemStats",
            "/DriverStation",
            "/RealMetadata",
            "/ReplayMetadata"
    );

    /**
     * Strategy for loading and saving the comma-separated allowlist string. Wraps either
     * WPILib {@link Preferences} (production) or a trivial in-memory store (tests).
     */
    public interface AllowlistStore {
        /** @return The persisted comma-separated allowlist, or an empty string if none. */
        String load();

        /** Persist the comma-separated allowlist. */
        void save(String value);
    }

    /** Default store that reads and writes WPILib {@link Preferences}. */
    public static final class PreferencesAllowlistStore implements AllowlistStore {
        private final String key;

        public PreferencesAllowlistStore() {
            this(PREFERENCES_KEY);
        }

        public PreferencesAllowlistStore(String key) {
            this.key = key;
        }

        @Override
        public String load() {
            return Preferences.containsKey(key) ? Preferences.getString(key, "") : "";
        }

        @Override
        public void save(String value) {
            Preferences.setString(key, value);
        }
    }

    private final AllowlistStore store;
    private volatile List<String> allowedPrefixes = List.of();
    private String lastStoreValue = "";
    private final ConcurrentSkipListSet<String> seenKeys = new ConcurrentSkipListSet<>();

    private StringArrayPublisher availableKeysPublisher;

    private long lastCatalogPublishMs = 0;
    private int lastCatalogSize = -1;

    /** Production constructor — backs the filter with WPILib {@link Preferences}. */
    public NetworkTablesPublishFilter() {
        this(new PreferencesAllowlistStore());
    }

    /** Testable constructor — accepts any {@link AllowlistStore} implementation. */
    public NetworkTablesPublishFilter(AllowlistStore store) {
        this.store = store;
        loadFromStore();
    }

    /**
     * @return The currently allowed user-specified prefixes (does not include the always-on set).
     */
    public List<String> getAllowedPrefixes() {
        return new ArrayList<>(allowedPrefixes);
    }

    /**
     * Replace the user-specified allowed prefixes with the given list. Persists the new value
     * via the configured {@link AllowlistStore}.
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
        String serialized = serialize(normalized);
        if (!serialized.equals(lastStoreValue)) {
            store.save(serialized);
            lastStoreValue = serialized;
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
     * Connects to NetworkTables and starts publishing the available-keys catalog. Must be called
     * once before {@link #periodic()} will publish.
     */
    public synchronized void connectToNetworkTables() {
        connectToNetworkTables(NetworkTableInstance.getDefault());
    }

    synchronized void connectToNetworkTables(NetworkTableInstance instance) {
        NetworkTable table = instance.getTable(NT_TABLE);
        availableKeysPublisher = table.getStringArrayTopic(NT_AVAILABLE_KEYS).publish();
    }

    /**
     * Call once per robot loop. Picks up dashboard-driven edits to the allowlist (via the
     * store) and publishes the available-keys catalog (debounced to roughly 1 Hz).
     */
    public synchronized void periodic() {
        periodic(System.currentTimeMillis());
    }

    synchronized void periodic(long nowMs) {
        // Pick up dashboard edits to the persisted allowlist.
        String latest = store.load();
        if (latest != null && !latest.equals(lastStoreValue)) {
            lastStoreValue = latest;
            List<String> parsed = parse(latest);
            if (!parsed.equals(allowedPrefixes)) {
                this.allowedPrefixes = parsed;
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
    }

    private void loadFromStore() {
        String raw = store.load();
        if (raw == null) {
            raw = "";
        }
        lastStoreValue = raw;
        List<String> parsed = parse(raw);
        this.allowedPrefixes = parsed;
        if (!parsed.isEmpty()) {
            log.info("Loaded {} allowed NT prefix(es) from store.", parsed.size());
        }
    }

    private List<String> parse(String value) {
        if (value == null || value.isEmpty()) {
            return List.of();
        }
        String[] parts = value.split(",");
        List<String> list = new ArrayList<>(parts.length);
        Collections.addAll(list, parts);
        return normalize(list);
    }

    private String serialize(List<String> prefixes) {
        return String.join(",", prefixes);
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
}
