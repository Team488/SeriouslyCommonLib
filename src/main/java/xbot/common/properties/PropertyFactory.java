package xbot.common.properties;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import xbot.common.logging.RobotAssertionManager;
import xbot.common.properties.Property.PropertyLevel;
import xbot.common.properties.Property.PropertyPersistenceType;

public class PropertyFactory {

    protected Logger log;
    private final XPropertyManager propertyManager;
    private String prefix = "";
    private RobotAssertionManager assertionManager;
    private boolean prefixSet;

    @Inject
    PropertyFactory(XPropertyManager propertyManager, RobotAssertionManager assertionManager) {
        this.propertyManager = propertyManager;
        this.assertionManager = assertionManager;
        log = Logger.getLogger(PropertyFactory.class);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
        prefixSet = true;
    }

    public void setPrefix(IPropertySupport prefixSource) {
        this.prefix = prefixSource.getPrefix();
        prefixSet = true;
    }

    public void appendPrefix(String toAppend) {
        prefix = prefix + "/" + toAppend;
        prefixSet = true;
    }

    public void setTopLevelPrefix() {
        prefix = "";
        prefixSet = true;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String createFullKey(String key) {
        String fullKey = null;
        if(this.prefix == null || this.prefix.isEmpty()) {
            fullKey = key;
        }
        else if (prefix.charAt(prefix.length() -1) == '/')
        {
            // If somebody already put a slash as a trailing character, then we don't have much to do.
            fullKey = this.getPrefix() + key;
        } else {
            fullKey = this.getPrefix() + "/" + key;
        }
        // We've seen issues with badly assembled keys where slashes are getting doubled up
        String cleanedKey = fullKey.replaceAll("/+", "/");
        if (fullKey != cleanedKey) {
            //log.warn("Property key '" + fullKey + "' had double slashes that were stripped out. Please fix the key logic to not create double slashes.");
        }
        return cleanedKey;
    }

    private void checkPrefixSet() {
        if (!prefixSet) {
            assertionManager.fail("You should always call setPrefix() on PropertyFactory before creating new properties; "
            + "otherwise, all properties will be at the root level."
            + "If you meant to have the property at the root level, call setTopLevelPrefix() first."
            );
        }
    }

    /**
     * Method for creating a boolean ephemeral property
     * 
     * @author Marc
     */
    public BooleanProperty createEphemeralProperty(String key, boolean defaultValue) {
        checkPrefixSet();
        return new BooleanProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager);
    }

    /**
     * Method for creating a boolean ephemeral property
     * 
     * @author Marc
     */
    public BooleanProperty createEphemeralProperty(String key, boolean defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new BooleanProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager, level);
    }

    /**
     * Method for creating a string ephemeral property
     * 
     * @author Marc
     */
    public StringProperty createEphemeralProperty(String key, String defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new StringProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager);
    }

    /**
     * Method for creating a string ephemeral property
     * 
     * @author Marc
     */
    public StringProperty createEphemeralProperty(String key, String defaultValue) {
        checkPrefixSet();
        return new StringProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager);
    }

    /**
     * Method for creating a double ephemeral property
     * 
     * @author Marc
     */
    public DoubleProperty createEphemeralProperty(String key, double defaultValue) {
        checkPrefixSet();
        return new DoubleProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager);
    }

    /**
     * Method for creating a double ephemeral property
     * 
     * @author Marc
     */
    public DoubleProperty createEphemeralProperty(String key, double defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new DoubleProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager, level);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public BooleanProperty createPersistentProperty(String key, boolean defaultValue) {
        checkPrefixSet();
        return new BooleanProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public BooleanProperty createPersistentProperty(String key, boolean defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new BooleanProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager, level);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public StringProperty createPersistentProperty(String key, String defaultValue) {
        checkPrefixSet();
        return new StringProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public StringProperty createPersistentProperty(String key, String defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new StringProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager, level);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public DoubleProperty createPersistentProperty(String key, double defaultValue) {
        checkPrefixSet();
        return new DoubleProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public DoubleProperty createPersistentProperty(String key, double defaultValue, PropertyLevel level) {
        checkPrefixSet();
        return new DoubleProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager, level);
    }


}