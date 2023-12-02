/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
     * The key for the property.
     */
    public final String key;

    protected final PermanentStorage permanentStore;
    protected final ITableProxy randomAccessStore;
    
    /** 
     * Enum to determine property persistence
     * Ephemeral properties will not be saved or loaded from a persistent storage.
     * Persistent properties will be saved and loaded from a persistent storage.
     */
    public enum PropertyPersistenceType{
        Ephemeral,
        Persistent
    }

    public enum PropertyLevel {
        Important,
        Debug
    }
    
    public final PropertyPersistenceType persistenceType;

    protected static Logger log;

    /**
     * Creates a new property.
     * @param key The property key.
     *            This should be unique unless you really know what you're doing.
     * @param manager The property manager.
     * @param persistenceType The persistence type.
     * @param level The property level.
     */
    public Property(String key, XPropertyManager manager, PropertyPersistenceType persistenceType, PropertyLevel level) {
        this.key = sanitizeKey(key);
        log = LogManager.getLogger(this.getClass().getSimpleName() + " (\"" + this.key + "\")");
        
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
     * Creates a new property.
     * @param key The property key.
     *            This should be unique unless you really know what you're doing.
     * @param manager The property manager.
     * @param persistenceType The persistence type.
     */
    public Property(String key, XPropertyManager manager, PropertyPersistenceType persistenceType) {
        this(key, manager, persistenceType, PropertyLevel.Important);
    }

    /**
     * Creates a new persistent property.
     * @param key The property key.
     *            This should be unique unless you really know what you're doing.
     * @param manager The property manager.
     */
    public Property(String key, XPropertyManager manager) {
        this(key, manager, PropertyPersistenceType.Persistent);
    } 

    private String sanitizeKey(String key) {
        String sanitizedKey = key;
        sanitizedKey = sanitizedKey.replace(",", "");
        sanitizedKey = sanitizedKey.replace("\n", "");
        if (!sanitizedKey.equals(key)) {
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
