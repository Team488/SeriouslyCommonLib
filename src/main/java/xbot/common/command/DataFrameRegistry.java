package xbot.common.command;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import xbot.common.advantage.DataFrameRefreshable;

/**
 * A registry for components that implement {@link DataFrameRefreshable}, which allows them to be refreshed on a regular basis by the {@link BaseRobot}.
 */
@Singleton
public final class DataFrameRegistry {
    private final List<DataFrameRefreshable> refreshables = new ArrayList<>();

    @Inject
    public DataFrameRegistry() {}

    public void register(DataFrameRefreshable refreshable) {
        if (refreshables.contains(refreshable)) {
            return;
        }
        refreshables.add(refreshable);
    }
    
    public void refreshAll() {
        for (DataFrameRefreshable refreshable : refreshables) {
            refreshable.refreshDataFrame();
        }
    }
}