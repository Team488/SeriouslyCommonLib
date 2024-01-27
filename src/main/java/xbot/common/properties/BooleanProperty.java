package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

/**
 * A property holding a boolean value.
 * 
 * @author Sterling
 */
public class BooleanProperty extends Property {
    private boolean defaultValue;

    boolean currentValue;

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue = table.get(suffix, defaultValue);
        }
    };

    public BooleanProperty(String prefix, String name, boolean defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public BooleanProperty(String prefix, String name, boolean defaultValue,
            XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, manager, level);
        this.defaultValue = defaultValue;

        Boolean firstValue = get_internal();
        if (firstValue != defaultValue) {
            log.info("Property " + key + " has the non-default value " + firstValue.booleanValue());
        }
        currentValue = firstValue;
    }

    public boolean get() {
        return currentValue;
    }

    /**
     * 
     * @return the current boolean value
     */
    public boolean get_internal() {
        Boolean nullableTableValue = activeStore.getBoolean(key);
        
        if(nullableTableValue == null) {
            log.error("Property key \"" + key + "\" not present in the underlying store!"
                    + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            set(defaultValue);
            return defaultValue;
        }
        
        return nullableTableValue.booleanValue();
    }

    /**
     *
     * @param value
     *            the value to set
     */
    public void set(boolean value) {
        activeStore.setBoolean(key, value);
        currentValue = value;
    }

    @Override
    public boolean isSetToDefault() {
        return get() == defaultValue;
    }

    @Override
    public void refreshDataFrame() {
        currentValue = get_internal();
        Logger.processInputs(prefix, inputs);
    }
}
