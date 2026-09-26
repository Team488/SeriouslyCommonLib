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
     * Namespace used for the AdvantageKit log subtable that Property values flow through.
     * <p>
     * Every Property records its current value as a {@code LoggableInputs} via
     * {@code Logger.processInputs(akitLogPrefix(), inputs)} so replay sees the value the robot
     * actually used. The AKit-side mirror is then forwarded to NetworkTables by default — which
     * is redundant, because dashboards already see the value via WPILib's {@code /Preferences/...}
     * surface. Routing the Property log entries through this dedicated subtable lets the NT
     * publisher drop them by prefix without touching the on-disk log receiver (which still gets
     * everything, keeping replay correct).
     * <p>
     * The name {@code PropertyMirror} (rather than just {@code Properties}) avoids confusion with
     * the WPILib {@code Preferences} table that lives at {@code /Preferences/...} in NetworkTables.
     */
    public static final String AKIT_LOG_NAMESPACE = "PropertyMirror/";

    /**
     * The key for the property.
     */
    public final String key;

    public final String prefix;

    public final String suffix;

    public final PropertyLevel level;
    protected ITableProxy activeStore;
    
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
     * @param prefix The property prefix.
     *               This should be unique unless you really know what you're doing.
     * @param suffix The property suffix.
     *               This should be unique unless you really know what you're doing.
     * @param manager The property manager.
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
     * @return The full prefix this Property uses when calling
     *     {@code Logger.processInputs(...)}. Subclasses should pass this to
     *     {@code Logger.processInputs} so their LogTable entries land under the
     *     {@link #AKIT_LOG_NAMESPACE Properties/} subtable rather than mixing in with
     *     subsystem telemetry.
     */
    protected String akitLogPrefix() {
        return AKIT_LOG_NAMESPACE + prefix;
    }

    /**
     * Updates the backing store for debug-level properties. Called by XPropertyManager
     * when the global "show all debug properties" flag changes.
     * @param newStore The new store to use.
     */
    void updateActiveStore(ITableProxy newStore) {
        if (this.level == PropertyLevel.Debug) {
            this.activeStore = newStore;
        }
    }

    /**
     * Creates a new property.
     * @param prefix The property prefix.
     *            This should be unique unless you really know what you're doing.
     * @param key The property key.
     *            This should be unique unless you really know what you're doing.
     * @param manager The property manager.
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
