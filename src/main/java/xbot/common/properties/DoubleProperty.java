package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import java.util.function.DoubleConsumer;

/**
 * This manages a double in the property system.
 *
 * @author Alex
 */
public class DoubleProperty extends Property {
    private final double defaultValue;
    double lastValue;

    double currentValue;

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue = table.get(suffix, defaultValue);
        }
    };

    public DoubleProperty(String prefix, String name, double defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public DoubleProperty(String prefix, String name, double defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, manager, level);
        this.defaultValue = defaultValue;

        // Check for non-default on load; also store a "last value" we can use
        // to check if a property has changed recently.
        double firstValue = get_internal();
        if (get_internal() != defaultValue) {
            log.info("Property " + key + " has the non-default value " + firstValue);
        }
        lastValue = firstValue;
        currentValue = firstValue;
    }

    public double get() {
        return currentValue;
    }


    public double get_internal() {
        Double nullableTableValue = activeStore.getDouble(key);

        if(nullableTableValue == null) {
            //log.error("Property key \"" + key + "\" not present in the underlying store!"
            //        + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            set(defaultValue);
            return defaultValue;
        }

        return nullableTableValue;
    }

    public void set(double value) {
        activeStore.setDouble(key, value);
        currentValue = value;
    }

    public void hasChangedSinceLastCheck(DoubleConsumer callback) {
        double currentValue = get();
        // TODO: Check if we can just use direct equality here, since we are in fact comparing a value to itself.
        if (Math.abs(currentValue - lastValue) > 0.00000000001) {
            callback.accept(currentValue);
        }
        lastValue = currentValue;
    }

    public boolean hasChangedSinceLastCheck() {
        double currentValue = get();
        // TODO: Check if we can just use direct equality here, since we are in fact comparing a value to itself.
        boolean changed = (Math.abs(currentValue - lastValue) > 0.00000000001);
        lastValue = currentValue;
        return changed;
    }

    public boolean isSetToDefault() {
        return get() == defaultValue;
    }

    @Override
    public void refreshDataFrame() {
        currentValue = get_internal();
        Logger.processInputs(prefix, inputs);
    }
}
