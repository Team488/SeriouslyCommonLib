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
            permanentStore.setDouble(key, get());
        }
    }

    /**
     *
     */
    public void load() {
        Double value = permanentStore.getDouble(key);
        if(value != null) {
            randomAccessStore.setDouble(key, value.doubleValue());
        } else {
            set(defaultValue);
        }
    }
    
    public void hasChangedSinceLastCheck(Consumer<Double> callback) {
        double currentValue = get();
        if (Math.abs(currentValue - lastValue) > 0.001) {
            callback.accept(currentValue);
        }
        lastValue = currentValue;
    }
}
