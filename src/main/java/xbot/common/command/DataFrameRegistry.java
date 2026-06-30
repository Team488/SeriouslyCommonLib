package xbot.common.command;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.advantage.DataFrameRefreshable;

/**
 * A registry for components that implement {@link DataFrameRefreshable}, which allows them to be refreshed on a regular basis by the {@link BaseRobot}.
 */
@Singleton
public final class DataFrameRegistry {
    // LinkedHashSet gives O(1) duplicate-registration checks while preserving registration order,
    // which refreshAll() relies on (devices are registered, and thus refreshed, before the
    // higher-level subsystems that read their freshly-refreshed data).
    private final Set<DataFrameRefreshable> refreshables = new LinkedHashSet<>();

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