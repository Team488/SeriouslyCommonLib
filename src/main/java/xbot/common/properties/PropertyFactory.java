package xbot.common.properties;

import com.google.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.properties.Property.PropertyPersistenceType;

public class PropertyFactory {

    private final XPropertyManager propertyManager;
    private String prefix = "";

    @Inject
    PropertyFactory(XPropertyManager propertyManager) {
        this.propertyManager = propertyManager;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setPrefix(BaseCommand baseCommand) {
        this.prefix = baseCommand.getPrefix();
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String createFullKey(String key) {
        if(this.prefix == null || this.prefix.isEmpty()) {
            return key;
        }
        return this.getPrefix() + "/" + key;
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