package xbot.common.properties;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

/**
 * The PropertyManager keeps track of all properties in CoreCode. All properties are implicitly added into its storage.
 * It is capable of loading or saving all properties via permanentStorage, and getting updates to these properties via
 * the RandomAccessStore.
 * 
 * @author Alex
 */
@Singleton
public class XPropertyManager {
    public static final String IN_MEMORY_STORE_NAME = "InMemoryStore";
    private static final Logger log = Logger.getLogger(XPropertyManager.class);

    public final ArrayList<Property> properties;
    public final PermanentStorage permanentStore;
    public final ITableProxy randomAccessStore;
    public final ITableProxy inMemoryRandomAccessStore;

    @Inject
    public XPropertyManager(
        PermanentStorage permanentStore, 
        ITableProxy randomAccessStore, 
        @Named(IN_MEMORY_STORE_NAME) ITableProxy inMemoryRandomAccessStore
    ) {
        this.properties = new ArrayList<Property>();
        this.permanentStore = permanentStore;
        this.randomAccessStore = randomAccessStore;
        this.inMemoryRandomAccessStore = inMemoryRandomAccessStore;
    }

    /**
     * Adds the property to the local collection.
     */
    public void registerProperty(Property property) {
        properties.add(property);
    }

    /**
     * Loads all properties from storage into the PermanentStore's table.
     */
    public void loadPropertiesFromStorage() {
        // We need to somehow get the random store and force load everything in that.
        int escape = 0;
        for (int i = 0; i < properties.size(); i++) {
            Property prop = (Property) properties.get(i);
            prop.load();

            escape++;
            if (escape > 2000) {
                break;
            }
        }
    }

    /**
     * Save all properties into permanent storage.
     */
    public void saveOutAllProperties() {

        // We should clear the permanent storage before we save, otherwise we can have
        // "orphaned" values that are loaded/saved indefinitely, even if there's nothing in the
        // code that uses them.

        if (properties.size() == 0) {
            log.error("No properties to save! Skipping save phase.");
            return;
        }

        int escape = 0;

        for (int i = 0; i < properties.size(); i++) {
            Property prop = (Property) properties.get(i);
            prop.save();

            escape++;
            if (escape > 2000) {
                break;
            }
        }
    }
}
