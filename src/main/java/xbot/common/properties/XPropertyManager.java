package xbot.common.properties;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
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

    @Inject
    public XPropertyManager(
        PermanentStorage permanentStore
    ) {
        this.properties = new ArrayList<>();
        this.permanentStore = permanentStore;
        this.inMemoryRandomAccessStore = new TableProxy();
    }

    /**
     * Adds the property to the local collection.
     */
    public void registerProperty(Property property) {
        properties.add(property);
    }

    /**
     * Must be called every robot loop, and should be done before any properties are accessed in order to
     * make the robot as responsive as possible.
     */
    @Override
    public void refreshDataFrame() {
        // Only refresh persistent/configuration properties.
        for (Property property : properties) {
            if (property.level == Property.PropertyLevel.Important) {
                property.refreshDataFrame();
            }
        }
    }
}
