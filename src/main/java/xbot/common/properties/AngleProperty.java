package xbot.common.properties;

import java.util.function.Consumer;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.measure.Angle;


import xbot.common.logging.Pluralizer;

/**
 * This manages an Angle in the property system.
 *
 * @author Alex
 */
public class AngleProperty extends Property {
    final Angle defaultValue;
    final AngleUnit defaultUnit;
    Angle lastValue;
    Angle currentValue;

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue = table.get(suffix, defaultValue);
        }
    };

    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name  + "-in-" + Pluralizer.pluralize(defaultValue.unit().name()), manager, level);
        this.defaultValue = defaultValue;
        this.defaultUnit = defaultValue.unit();

        // Check for non-default on load; also store a "last value" we can use
        // to check if a property has changed recently.
        Angle firstValue = get_internal();
        if (get_internal() != defaultValue) {
            log.info("Property " + key + " has the non-default value " + firstValue);
        }
        lastValue = firstValue;
        currentValue = firstValue;
    }

    public Angle get() {
        return currentValue;
    }


    public Angle get_internal() {
        Double nullableTableValue = activeStore.getDouble(key);

        if(nullableTableValue == null) {
            //log.error("Property key \"" + key + "\" not present in the underlying store!"
            //        + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            set(defaultValue);
            return defaultValue;
        }

        return defaultUnit.of(nullableTableValue);
    }

    public void set(Angle value) {
        activeStore.setDouble(key, value.in(defaultUnit));
        currentValue = value;
    }

    public void hasChangedSinceLastCheck(Consumer<Angle> callback) {
        Angle currentValue = get();
        if (!currentValue.isEquivalent(lastValue)) {
            callback.accept(currentValue);
        }
        lastValue = currentValue;
    }

    public boolean hasChangedSinceLastCheck() {
        Angle currentValue = get();
        boolean changed = !currentValue.isEquivalent(lastValue);
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
