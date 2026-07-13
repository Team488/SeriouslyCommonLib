package xbot.common.command;

import java.util.LinkedHashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.advantage.DataFrameRefreshable;

/**
 * A registry for components that implement {@link DataFrameRefreshable}, which allows them to be refreshed on a regular basis by the {@link BaseRobot}.
 */
@Singleton
public final class DataFrameRegistry {
    // Declared as LinkedHashSet (not the Set interface) because refreshAll() relies on
    // registration-order iteration: devices are registered, and thus refreshed, before the
    // higher-level subsystems that read their freshly-refreshed data. Set makes no such
    // ordering promise, so swapping the declared type would silently permit an unordered
    // implementation (e.g. HashSet) to break that guarantee.
    private final LinkedHashSet<DataFrameRefreshable> refreshables = new LinkedHashSet<>();

    @Inject
    public DataFrameRegistry() {}

    public void register(DataFrameRefreshable refreshable) {
        refreshables.add(refreshable);
    }

    public void refreshAll() {
        for (DataFrameRefreshable refreshable : refreshables) {
            refreshable.refreshDataFrame();
        }
    }
}