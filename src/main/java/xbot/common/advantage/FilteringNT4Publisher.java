package xbot.common.advantage;

import org.littletonrobotics.junction.LogDataReceiver;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.LogTable.LogValue;
import org.littletonrobotics.junction.networktables.NT4Publisher;

import java.util.Map;

/**
 * A {@link LogDataReceiver} that wraps {@link NT4Publisher} and forwards only the keys
 * permitted by a {@link NetworkTablesPublishFilter}. Every key seen is recorded with the
 * filter so dashboards can discover what's available to allowlist.
 *
 * <p>The on-disk log receiver (e.g. {@code WPILOGWriter}) is unaffected — only the
 * NetworkTables publication path is filtered.
 */
public class FilteringNT4Publisher implements LogDataReceiver {

    private final NetworkTablesPublishFilter filter;
    private final LogDataReceiver wrapped;

    public FilteringNT4Publisher(NetworkTablesPublishFilter filter) {
        this(filter, new NT4Publisher());
    }

    /** Visible for testing. */
    FilteringNT4Publisher(NetworkTablesPublishFilter filter, LogDataReceiver wrapped) {
        this.filter = filter;
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
            filter.recordSeenKey(key);
            if (filter.shouldPublish(key)) {
                // LogTable.put prepends the root prefix "/" to the key, so strip the
                // leading slash we got from getAll() to avoid producing "//Foo".
                String relativeKey = key.startsWith("/") ? key.substring(1) : key;
                filtered.put(relativeKey, entry.getValue());
            }
        }
        wrapped.putTable(filtered);
    }
}
