/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import org.apache.log4j.Logger;

/**
 * There are many values on the robot that we want to configure on the fly as
 * well as persist once we're happy with the result. We call these Properties.
 * They can be read/written to rapidly using a RandomAccessStore, and know how
 * to save themselves to PermanentStorage when a save is necessary.
 * 
 * @author Alex
 */
public abstract class Property {
    /**
     *
     */
    public String key;

    PermanentStorage permanentStore;
    ITableProxy randomAccessStore;
    
    /** 
     * New enum to determine property persistence
     * Ephemeral properties will not be saved or load from a persistent storage
     * Persistent properties will be saved and load from a persistent storage
     */
    public enum PropertyPersistenceType{
        Ephemeral,
        Persistent
    }

    public enum PropertyLevel {
        Important,
        Debug
    }
    
    public PropertyPersistenceType persistenceType;
    public PropertyLevel level;
    protected static Logger log;

    /**
     * The name of the property. This should be unique unless you really know
     * what you're doing.
     * New builder with persistence type. Old builder will be deprecated.
     * @author Marc
     */
    public Property(String key, XPropertyManager manager, PropertyPersistenceType persistenceType, PropertyLevel level) {
        this.key = sanitizeKey(key);
        log =  Logger.getLogger(this.getClass().getSimpleName() + " (\"" + this.key + "\")");
        
        this.permanentStore = manager.permanentStore;

        if(level == PropertyLevel.Debug){
            this.randomAccessStore = manager.inMemoryRandomAccessStore;
        } else {
            this.randomAccessStore = manager.randomAccessStore;
        }

        this.persistenceType = persistenceType;
        manager.registerProperty(this);
    }
    
    /**
     * The name of the property. This should be unique unless you really know
     * what you're doing.
     * New builder with persistence type. Old builder will be deprecated.
     * @author Marc
     */
    public Property(String key, XPropertyManager manager, PropertyPersistenceType persistenceType) {
        this(key, manager, persistenceType, PropertyLevel.Important);
    }

    /**
     * The name of the property. This should be unique unless you really know
     * what you're doing.
     * 
     */
    public Property(String key, XPropertyManager manager) {
        this(key, manager, PropertyPersistenceType.Persistent);
    } 

    private String sanitizeKey(String key) {
        String sanitizedKey = key;
        sanitizedKey = sanitizedKey.replace(",", "");
        sanitizedKey = sanitizedKey.replace("\n", "");
        if (sanitizedKey != key) {
            log.warn(String
                    .format("Property '%s' contained illegal characters, has been sanitzed to '%s'",
                            key, sanitizedKey));
        }
        return sanitizedKey;
    }

    /**
     * Save the property permanently. This shouldn't happen very often (I/O is
     * expensive).
     */
    public abstract void save();

    /**
     * Load the property from storage. This shouldn't happen very often (I/O is
     * expensive).
     */
    public abstract void load();

    /**
     * Checks if the property's current value matches the default.
     * @return True if the current value is the default.
     */
    public abstract boolean isSetToDefault();
}
