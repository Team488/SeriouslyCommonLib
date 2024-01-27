/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.advantage.DataFrameRefreshable;

/**
 * There are many values on the robot that we want to configure on the fly as
 * well as persist once we're happy with the result. We call these Properties.
 * They can be read/written to rapidly using a RandomAccessStore, and know how
 * to save themselves to PermanentStorage when a save is necessary.
 * 
 * @author Alex
 */
public abstract class Property implements DataFrameRefreshable {
    /**
     * The key for the property.
     */
    public final String key;

    public final String prefix;

    public final String suffix;

    public final PropertyLevel level;
    protected final ITableProxy activeStore;
    
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

    protected Logger log = LogManager.getLogger(this.getClass());

    /**
     * Creates a new property.
     * @param key The property key.
     *            This should be unique unless you really know what you're doing.
     * @param manager The property manager.
     * @param persistenceType The persistence type.
     * @param level The property level.
     */
    public Property(String prefix, String suffix, XPropertyManager manager, PropertyLevel level) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.key = sanitizeFullKey(prefix + suffix);
        this.level = level;

        log = LogManager.getLogger(this.getClass().getSimpleName() + " (\"" + this.key + "\")");

        if(level == PropertyLevel.Debug){
            this.activeStore = manager.inMemoryRandomAccessStore;
        } else {
            this.activeStore = manager.permanentStore;
        }

        manager.registerProperty(this);
    }

    public PropertyLevel getLevel() {
        return level;
    }

    /**
     * Creates a new property.
     * @param key The property key.
     *            This should be unique unless you really know what you're doing.
     * @param manager The property manager.
     * @param persistenceType The persistence type.
     */
    public Property(String prefix, String key, XPropertyManager manager) {
        this(prefix, key, manager, PropertyLevel.Important);
    }

    private String sanitizeFullKey(String key) {
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
     * Checks if the property's current value matches the default.
     * @return True if the current value is the default.
     */
    public abstract boolean isSetToDefault();
}
