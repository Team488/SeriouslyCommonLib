/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import java.util.function.Consumer;

/**
 * This manages a double in the property system.
 * 
 * @author Alex
 */
public class DoubleProperty extends Property {
    double defaultValue;
    double lastValue;

    public DoubleProperty(String name, double defaultValue, XPropertyManager manager) {
        super(name, manager);
        this.defaultValue = defaultValue;
        load();
        lastValue = get();
    }
    
    public DoubleProperty(String name, double defaultValue, PropertyPersistenceType persistenceType, XPropertyManager manager) {
        super(name, manager, persistenceType);
        this.defaultValue = defaultValue;
        load();
        lastValue = get();
    }

    public DoubleProperty(String name, double defaultValue, PropertyPersistenceType persistenceType, XPropertyManager manager, PropertyLevel level) {
        super(name, manager, persistenceType, level);
        this.defaultValue = defaultValue;
        load();
        lastValue = get();
    }
    

    public double get() {
        Double nullableTableValue = randomAccessStore.getDouble(key);
        
        if(nullableTableValue == null) {
            log.error("Property key \"" + key + "\" not present in the underlying store!"
                    + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            return defaultValue;
        }
        
        return nullableTableValue.doubleValue();
    }
    
    public void set(double value) {
        randomAccessStore.setDouble(key, value);
    }
    
    /**
     * We only save the property if it's from a persistent type
     */
    public void save() {
        if(persistenceType == PropertyPersistenceType.Persistent) {
            if (!isSetToDefault()) {
                permanentStore.setDouble(key, get());
            } else {
                permanentStore.remove(key);
            }
        }
    }

    /**
     *
     */
    public void load() {
        Double value = permanentStore.getDouble(key);
        if(value != null) {
            log.info("Property " + key + " has the non-default value " + value.doubleValue());
            randomAccessStore.setDouble(key, value.doubleValue());
        } else {
            set(defaultValue);
        }
    }
    
    public void hasChangedSinceLastCheck(Consumer<Double> callback) {
        double currentValue = get();
        // TODO: Check if we can just use direct equality here, since we are in fact comparing a value to itself.
        if (Math.abs(currentValue - lastValue) > 0.00000000001) {
            callback.accept(currentValue);
        }
        lastValue = currentValue;
    }

    public boolean isSetToDefault() {
        return get() == defaultValue;
    }
}
