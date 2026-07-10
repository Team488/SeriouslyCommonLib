package xbot.common.properties;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.advantage.DataFrameRefreshable;

/**
 * The PropertyManager keeps track of all properties in CoreCode. All properties are implicitly added into its storage.
 * It is capable of loading or saving all properties via permanentStorage, and getting updates to these properties via
 * the RandomAccessStore.
 * 
 * @author Alex
 */
@Singleton
public class XPropertyManager implements DataFrameRefreshable {
    public static final String IN_MEMORY_STORE_NAME = "InMemoryStore";
    private static final Logger log = LogManager.getLogger(XPropertyManager.class);

    public final ArrayList<Property> properties;
    public final PermanentStorage permanentStore;
    public final ITableProxy inMemoryRandomAccessStore;
    private final BooleanProperty showDebugPropertiesProp;
    private boolean lastShowDebug = false;

    @Inject
    public XPropertyManager(
        PermanentStorage permanentStore
    ) {
        this.properties = new ArrayList<>();
        this.permanentStore = permanentStore;
        this.inMemoryRandomAccessStore = new TableProxy();
        this.showDebugPropertiesProp = new BooleanProperty(
                "Properties/", "ShowDebugProperties", false, this, Property.PropertyLevel.Important);
    }

    /**
     * Adds the property to the local collection.
     */
    public void registerProperty(Property property) {
        properties.add(property);
    }

    /**
     * Globally enables or disables showing debug-level properties in NetworkTables.
     * When enabled, debug properties behave like Important properties: they are
     * backed by the permanent store and refreshed every robot loop.
     * When disabled, they revert to the in-memory store and are no longer published.
     * @param show True to publish debug properties to NetworkTables.
     */
    public void setShowAllDebugProperties(boolean show) {
        showDebugPropertiesProp.set(show);
        ITableProxy targetStore = show ? permanentStore : inMemoryRandomAccessStore;
        for (Property property : properties) {
            property.updateActiveStore(targetStore);
        }
    }

    int propertyRefreshIndex = 0;

    /**
     * Must be called every robot loop, and should be done before any properties are accessed in order to
     * make the robot as responsive as possible.
     */
    @Override
    public void refreshDataFrame() {
        // Sync debug mode from the NetworkTables-visible control property every loop.
        showDebugPropertiesProp.refreshDataFrame();
        boolean showDebug = showDebugPropertiesProp.get();
        if (showDebug != lastShowDebug) {
            lastShowDebug = showDebug;
            setShowAllDebugProperties(showDebug);
        }

        if (propertyRefreshIndex > properties.size()-1) {
            propertyRefreshIndex = 0;
        }

        var property = properties.get(propertyRefreshIndex);
        // Skip the control property since it is already refreshed above.
        if (property != showDebugPropertiesProp
                && (property.level == Property.PropertyLevel.Important
                    || (property.level == Property.PropertyLevel.Debug && lastShowDebug))) {
            property.refreshDataFrame();
        }

        propertyRefreshIndex++;
    }
}
