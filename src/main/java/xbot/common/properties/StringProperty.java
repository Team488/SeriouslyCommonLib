/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xbot.common.properties;

/**
 * A type of Property that manages a String value.
 * @author Sterling
 */
public class StringProperty extends Property {
    private String defaultValue;
    
    public StringProperty(String name, String defaultValue, XPropertyManager manager) {
        super(name, manager);
        this.defaultValue = defaultValue;
        load();
    }
    
    public StringProperty(String name, String defaultValue, PropertyPersistenceType persistenceType, XPropertyManager manager) {
        super(name, manager, persistenceType);
        this.defaultValue = defaultValue;
        load();
    }

    public StringProperty(String name, String defaultValue, PropertyPersistenceType persistenceType, XPropertyManager manager, PropertyLevel level) {
        super(name, manager, persistenceType, level);
        this.defaultValue = defaultValue;
        load();
    }
    
    public String get() {
        String nullableTableValue = randomAccessStore.getString(key);
        
        if(nullableTableValue == null) {
            log.error("Property key \"" + key + "\" not present in the underlying store!"
                    + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            return defaultValue;
        }
        
        return nullableTableValue;
    }
    
    public void set(String value) {
        randomAccessStore.setString(key, value);
    }
    
    /**
     * We only save the property if it's from a persistent type
     */
    public void save() {
        if(persistenceType == PropertyPersistenceType.Persistent) {
            if (!isSetToDefault()) {
                permanentStore.setString(key, get());
            } else {
                permanentStore.remove(key);
            }
        }
    }

    public void load() {
        String value = permanentStore.getString(key);
        if(value != null) {
            set(value);
            log.info("Property " + key + " has the non-default value " + value);
        } else {
            set(defaultValue);
        }
    }

    public boolean isSetToDefault() {
        return get().equals(defaultValue);
    }
}
