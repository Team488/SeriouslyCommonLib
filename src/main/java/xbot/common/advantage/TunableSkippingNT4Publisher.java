package xbot.common.advantage;

import java.util.Map;

import org.littletonrobotics.junction.LogDataReceiver;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.LogTable.LogValue;
import org.littletonrobotics.junction.networktables.NT4Publisher;

import xbot.common.properties.TunableManager;

/**
 * A {@link LogDataReceiver} that wraps {@link NT4Publisher} and skips any LogTable entry
 * under the {@link TunableManager#AKIT_LOG_NAMESPACE} subtable.
 *
 * <p>Tunable values are routed through that subtable so the on-disk WPILOG receiver still
 * captures them (replay correctness), but the live NetworkTables surface stays clean —
 * dashboards see editable values through WPILib's {@code /Tunables/...} table.
 *
 * <p>All other keys (subsystem telemetry, {@code aKitLog.record(...)} outputs, system stats,
 * driver-station data, etc.) flow through unchanged.
 */
public class TunableSkippingNT4Publisher implements LogDataReceiver {

    /** LogTable keys have a leading slash; the namespace constant does not. */
    static final String DENY_PREFIX = "/" + TunableManager.AKIT_LOG_NAMESPACE;

    private final LogDataReceiver wrapped;

    public TunableSkippingNT4Publisher() {
        this(new NT4Publisher());
    }

    /** Visible for testing. */
    TunableSkippingNT4Publisher(LogDataReceiver wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void start() {
        wrapped.start();
    }

    @Override
    public void end() {
        wrapped.end();
    }

    @Override
    public void putTable(LogTable table) throws InterruptedException {
        LogTable filtered = new LogTable(table.getTimestamp());
        for (Map.Entry<String, LogValue> entry : table.getAll(false).entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(DENY_PREFIX)) {
                continue;
            }
            // LogTable.put prepends the root prefix "/" automatically, so strip the
            // leading slash we got from getAll() to avoid producing "//Foo".
            String relativeKey = key.startsWith("/") ? key.substring(1) : key;
            filtered.put(relativeKey, entry.getValue());
        }
        wrapped.putTable(filtered);
    }
}
