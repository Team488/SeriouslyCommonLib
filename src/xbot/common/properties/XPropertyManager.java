/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import xbot.common.properties.Property.PropertyPersistenceType;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The PropertyManager keeps track of all properties in CoreCode. All properties are implicitly added into its storage.
 * It is capable of loading or saving all properties via permanentStorage, and getting updates to these properties via
 * the RandomAccessStore.
 * 
 * @author Alex
 */
@Singleton
public class XPropertyManager {

    private static final Logger log = Logger.getLogger(XPropertyManager.class);

    /**
     *
     */
    public ArrayList<Property> properties;
    /**
     *
     */
    public PermanentStorage permanentStore;
    /**
     *
     */
    public ITableProxy randomAccessStore;

    @Inject
    public XPropertyManager(PermanentStorage permanentStore, ITableProxy randomAccessStore) {
        this.properties = new ArrayList<Property>();
        this.permanentStore = permanentStore;
        this.randomAccessStore = randomAccessStore;
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
            if (escape > 500) {
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
            if (escape > 500) {
                break;
            }
        }
    }

    /**
     * @deprecated You should use createProperty(String key, Boolean defaultValue, PropertyPersistenceType
     *             persistenceType) instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public BooleanProperty createProperty(String key, Boolean defaultValue) {
        return new BooleanProperty(key, defaultValue, this);
    }

    /**
     * @deprecated You should use createProperty(String key, String defaultValue, PropertyPersistenceType
     *             persistenceType) instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public StringProperty createProperty(String key, String defaultValue) {
        return new StringProperty(key, defaultValue, this);
    }

    /**
     * @deprecated You should use createProperty(String key, Double defaultValue, PropertyPersistenceType
     *             persistenceType) instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public DoubleProperty createProperty(String key, Double defaultValue) {
        return new DoubleProperty(key, defaultValue, this);
    }

    /**
     * Method for creating a boolean ephemeral property
     * 
     * @author Marc
     */
    public BooleanProperty createEphemeralProperty(String key, Boolean defaultValue) {
        return new BooleanProperty(key, defaultValue, PropertyPersistenceType.Ephemeral, this);
    }

    /**
     * Method for creating a string ephemeral property
     * 
     * @author Marc
     */
    public StringProperty createEphemeralProperty(String key, String defaultValue) {
        return new StringProperty(key, defaultValue, PropertyPersistenceType.Ephemeral, this);
    }

    /**
     * Method for creating a double ephemeral property
     * 
     * @author Marc
     */
    public DoubleProperty createEphemeralProperty(String key, Double defaultValue) {
        return new DoubleProperty(key, defaultValue, PropertyPersistenceType.Ephemeral, this);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public BooleanProperty createPersistentProperty(String key, Boolean defaultValue) {
        return new BooleanProperty(key, defaultValue, PropertyPersistenceType.Persistent, this);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public StringProperty createPersistentProperty(String key, String defaultValue) {
        return new StringProperty(key, defaultValue, PropertyPersistenceType.Persistent, this);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public DoubleProperty createPersistentProperty(String key, Double defaultValue) {
        return new DoubleProperty(key, defaultValue, PropertyPersistenceType.Persistent, this);
    }
}
