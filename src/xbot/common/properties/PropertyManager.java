/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import xbot.common.properties.PermanentStorageProxy;
import xbot.common.properties.Property.PropertyPersistenceType;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The PropertyManager keeps track of all properties in CoreCode. All properties are
 * implicitly added into its storage. It is capable of loading or saving all properties
 * via permanentStorage, and getting updates to these properties via the RandomAccessStore.
 * @author Alex
 */
@Singleton
public class PropertyManager {
    
    private static final Logger log = Logger
            .getLogger(PropertyManager.class);
    
    /**
     *
     */
    public ArrayList<Property> properties;
    /**
     *
     */
    public PermanentStorageProxy permanentStore;
    /**
     *
     */
    public ITableProxy randomAccessStore;

    @Inject
    public PropertyManager(PermanentStorageProxy permanentStore, ITableProxy randomAccessStore) {
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
    public void loadPropertiesFromStorage()
    {
        permanentStore.loadFromDisk();
        
        // We need to somehow get the random store and force load everything in that.
        int escape = 0;
        for(int i = 0; i < properties.size(); i++) {
            Property prop = (Property)properties.get(i);
            prop.load();
            
            escape++;
            if (escape > 500)
            {
                break;
            }
        }
    }
    
    /**
     * Save all properties into permanent storage.
     */
    public void saveOutAllProperties() {
        
        if (properties.size() == 0)
        {
            log.error("No properties to save! Skipping save phase.");
            return;
        }
        
        int escape = 0;
        
        for(int i = 0; i < properties.size(); i++) {
            Property prop = (Property)properties.get(i);
            prop.save();
            
            escape++;
            if (escape > 500)
            {
                break;
            }
        }
        
        // We also need to trigger the permanent storage proxy to actually write
        // to disk.
        permanentStore.saveToDisk();
    }
    
    /**
     * @deprecated You should use createProperty(String key, Boolean defaultValue, PropertyPersistenceType persistenceType) 
     * instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public BooleanProperty createProperty(String key, Boolean defaultValue) {
    	return new BooleanProperty(key, defaultValue, this);
    }
    /**
     * @deprecated You should use createProperty(String key, String defaultValue, PropertyPersistenceType persistenceType) 
     * instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public StringProperty createProperty(String key, String defaultValue) {
    	return new StringProperty(key, defaultValue, this);
    }
    /**
     * @deprecated You should use createProperty(String key, Double defaultValue, PropertyPersistenceType persistenceType) 
     * instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public DoubleProperty createProperty(String key, Double defaultValue) {
    	return new DoubleProperty(key, defaultValue, this);
    }
    
    /**
     * Creating new methods for creating the properties with a new parameter.
     * This parameter will determine if the property is ephemeral or have to be persisted.
     * Old methods with 2 parameters will be deprecated
     * @author Marc
     */
    public BooleanProperty createProperty(String key, Boolean defaultValue, PropertyPersistenceType persistenceType) {
    	return new BooleanProperty(key, defaultValue, persistenceType, this);
    } 
    
    public StringProperty createProperty(String key, String defaultValue, PropertyPersistenceType persistenceType) {
    	return new StringProperty(key, defaultValue, persistenceType, this);
    }
    
    public DoubleProperty createProperty(String key, Double defaultValue, PropertyPersistenceType persistenceType) {
    	return new DoubleProperty(key, defaultValue, persistenceType, this);
    }
    
    
}
