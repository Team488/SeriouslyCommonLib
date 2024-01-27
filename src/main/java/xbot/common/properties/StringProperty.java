/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

/**
 * A type of Property that manages a String value.
 * @author Sterling
 */
public class StringProperty extends Property {
    private final String defaultValue;

    String currentValue;

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue = table.get(suffix, defaultValue);
        }
    };
    
    public StringProperty(String prefix, String name, String defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public StringProperty(String prefix, String name, String defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, manager, level);
        this.defaultValue = defaultValue;


        String firstValue = get_internal();
        if (get_internal() != defaultValue) {
            log.info("Property " + key + " has the non-default value " + firstValue);
        }
        currentValue = firstValue;
    }

    public String get() {
        return currentValue;
    }
    
    public String get_internal() {
        String nullableTableValue = activeStore.getString(key);
        
        if(nullableTableValue == null) {
            log.error("Property key \"" + key + "\" not present in the underlying store!"
                    + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            set(defaultValue);
            return defaultValue;
        }
        
        return nullableTableValue;
    }
    
    public void set(String value) {
        activeStore.setString(key, value);
        currentValue = value;
    }

    public boolean isSetToDefault() {
        return get().equals(defaultValue);
    }

    @Override
    public void refreshDataFrame() {
        currentValue = get_internal();
        Logger.processInputs(prefix, inputs);
    }
}
