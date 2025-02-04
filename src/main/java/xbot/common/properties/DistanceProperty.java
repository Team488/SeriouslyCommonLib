/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xbot.common.properties;

import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import edu.wpi.first.units.DistanceUnit;
import edu.wpi.first.units.measure.Distance;

import java.util.function.Consumer;

/**
 * This manages a Distance  in the property system.
 *
 * @author Alex
 */
public class DistanceProperty extends Property {
    final Distance defaultValue;
    final DistanceUnit defaultUnit;
    Distance lastValue;
    Distance currentValue;

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue = table.get(suffix, defaultValue);
        }
    };

    public DistanceProperty(String prefix, String name, Distance defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public DistanceProperty(String prefix, String name, Distance defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name  + "-in-" + defaultValue.unit().name(), manager, level);
        this.defaultValue = defaultValue;
        this.defaultUnit = defaultValue.unit();

        // Check for non-default on load; also store a "last value" we can use
        // to check if a property has changed recently.
        Distance firstValue = get_internal();
        if (get_internal() != defaultValue) {
            log.info("Property " + key + " has the non-default value " + firstValue);
        }
        lastValue = firstValue;
        currentValue = firstValue;
    }

    public Distance get() {
        return currentValue;
    }


    public Distance get_internal() {
        Distance nullableTableValue = defaultUnit.of(activeStore.getDouble(key));

        if(nullableTableValue == null) {
            //log.error("Property key \"" + key + "\" not present in the underlying store!"
            //        + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            set(defaultValue);
            return defaultValue;
        }

        return nullableTableValue;
    }

    public void set(Distance value) {
        activeStore.setDouble(key, value.in(defaultUnit));
        currentValue = value;
    }

    public void hasChangedSinceLastCheck(Consumer<Distance> callback) {
        Distance currentValue = get();
        if (!currentValue.isEquivalent(lastValue)) {
            callback.accept(currentValue);
        }
        lastValue = currentValue;
    }

    public boolean hasChangedSinceLastCheck() {
        Distance currentValue = get();
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
