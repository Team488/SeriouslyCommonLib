/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xbot.common.properties;


/**
 * A property holding a boolean value.
 * 
 * @author Sterling
 */
public class BooleanProperty extends Property {
    private boolean defaultValue;

    /**
     *
     * @param name key of the property
     * @param defaultValue initial value if none there from the permanent store
     */
    public BooleanProperty(String name, boolean defaultValue,
            XPropertyManager manager) {
        super(name, manager);
        this.defaultValue = defaultValue;
        load();
    }
    
    public BooleanProperty(String name, boolean defaultValue, PropertyPersistenceType persistenceType,
            XPropertyManager manager) {
        super(name, manager, persistenceType);
        this.defaultValue = defaultValue;
        load();
    }

    /**
     * 
     * @return the current boolean value
     */
    public boolean get() {
        return randomAccessStore.getBoolean(key).booleanValue();
    }

    /**
     *
     * @param value
     *            the value to set
     */
    public void set(boolean value) {
        randomAccessStore.setBoolean(key, value);
    }

    /**
     * Saves the value permanently (presumably to the Robot).
     */
    public void save() {
        if(persistenceType == PropertyPersistenceType.Persistent) {
            permanentStore.setBoolean(key, randomAccessStore.getBoolean(key)
                .booleanValue());
        }
    }

    /**
     * Load the boolean value from the permanent store.
     */
    public void load() {
        Boolean value = permanentStore.getBoolean(key);
        if (value != null) {
            set(value.booleanValue());
        } else {
            set(defaultValue);
        }
    }
}
