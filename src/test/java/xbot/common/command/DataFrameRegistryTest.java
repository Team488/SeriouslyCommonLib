package xbot.common.command;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import xbot.common.advantage.DataFrameRefreshable;

import static org.junit.Assert.assertEquals;

public class DataFrameRegistryTest {

    private DataFrameRegistry registry;
    private List<String> refreshOrder;

    @Before
    public void setUp() {
        registry = new DataFrameRegistry();
        refreshOrder = new ArrayList<>();
    }

    @Test
    public void refreshesInRegistrationOrder() {
        // Higher-level subsystems (e.g. pose estimation) rely on refreshAll() refreshing their
        // dependencies (e.g. gyro, drive) first. That guarantee only holds if refreshables are
        // iterated in the order they were registered.
        registry.register(trackingRefreshable("gyro"));
        registry.register(trackingRefreshable("drive"));
        registry.register(trackingRefreshable("pose"));

        registry.refreshAll();

        assertEquals(List.of("gyro", "drive", "pose"), refreshOrder);
    }

    @Test
    public void duplicateRegistrationDoesNotReorderOrDoubleRefresh() {
        DataFrameRefreshable gyro = trackingRefreshable("gyro");
        registry.register(gyro);
        registry.register(trackingRefreshable("drive"));
        registry.register(gyro);

        registry.refreshAll();

        assertEquals(List.of("gyro", "drive"), refreshOrder);
    }

    private DataFrameRefreshable trackingRefreshable(String name) {
        return () -> refreshOrder.add(name);
    }
}
