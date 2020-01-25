package xbot.common.properties;

import com.google.inject.Inject;

import org.apache.log4j.Logger;

import xbot.common.command.BaseCommand;
import xbot.common.command.BaseSubsystem;
import xbot.common.properties.Property.PropertyPersistenceType;

public class PropertyFactory {

    protected Logger log;
    private final XPropertyManager propertyManager;
    private String prefix = "";

    @Inject
    PropertyFactory(XPropertyManager propertyManager) {
        this.propertyManager = propertyManager;
        log = Logger.getLogger(PropertyFactory.class);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPrefix(BaseCommand baseCommand) {
        this.prefix = baseCommand.getPrefix();
    }
    
    public void setPrefix(BaseSubsystem baseSubsystem) {
        this.prefix = baseSubsystem.getPrefix();
    }

    public void appendPrefix(String toAppend) {
        prefix = prefix + "/" + toAppend;
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
            log.warn("Property key '" + fullKey + "' had double slashes that were stripped out. Please fix the key logic to not create double slashes.");
        }
        return cleanedKey;
    }


    /**
     * @deprecated You should use createProperty(String key, boolean defaultValue, PropertyPersistenceType
     *             persistenceType) instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public BooleanProperty createProperty(String key, boolean defaultValue) {
        return new BooleanProperty(this.createFullKey(key), defaultValue, this.propertyManager);
    }

    /**
     * @deprecated You should use createProperty(String key, String defaultValue, PropertyPersistenceType
     *             persistenceType) instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public StringProperty createProperty(String key, String defaultValue) {
        return new StringProperty(this.createFullKey(key), defaultValue, this.propertyManager);
    }

    /**
     * @deprecated You should use createProperty(String key, double defaultValue, PropertyPersistenceType
     *             persistenceType) instead, which includes persistenceType to determine if the property is persistent
     * 
     */
    public DoubleProperty createProperty(String key, double defaultValue) {
        return new DoubleProperty(this.createFullKey(key), defaultValue, this.propertyManager);
    }

    /**
     * Method for creating a boolean ephemeral property
     * 
     * @author Marc
     */
    public BooleanProperty createEphemeralProperty(String key, boolean defaultValue) {
        return new BooleanProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager);
    }

    /**
     * Method for creating a string ephemeral property
     * 
     * @author Marc
     */
    public StringProperty createEphemeralProperty(String key, String defaultValue) {
        return new StringProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager);
    }

    /**
     * Method for creating a double ephemeral property
     * 
     * @author Marc
     */
    public DoubleProperty createEphemeralProperty(String key, double defaultValue) {
        return new DoubleProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Ephemeral, this.propertyManager);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public BooleanProperty createPersistentProperty(String key, boolean defaultValue) {
        return new BooleanProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public StringProperty createPersistentProperty(String key, String defaultValue) {
        return new StringProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager);
    }

    /**
     * Method for creating a double persistent property
     * 
     * @author Marc
     */
    public DoubleProperty createPersistentProperty(String key, double defaultValue) {
        return new DoubleProperty(this.createFullKey(key), defaultValue, PropertyPersistenceType.Persistent, this.propertyManager);
    }


}