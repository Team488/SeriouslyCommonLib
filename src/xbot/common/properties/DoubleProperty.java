/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

/**
 * This manages a double in the property system.
 * @author Alex
 */
public class DoubleProperty extends Property {
    double defaultValue;

    public DoubleProperty(String name, double defaultValue, PropertyManager manager) {
        super(name, manager);
        this.defaultValue = defaultValue;
        load();
    }
    

    public double get() {
        return randomAccessStore.getDouble(key).doubleValue();
    }
    

    public void set(double value) {
        randomAccessStore.setDouble(key, value);
    }
    
    /**
     *
     */
    public void save() {
        permanentStore.setDouble(key, randomAccessStore.getDouble(key).doubleValue());
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
    
}
