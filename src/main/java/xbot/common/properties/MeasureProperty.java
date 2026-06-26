package xbot.common.properties;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.MutableMeasure;
import edu.wpi.first.units.Unit;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import xbot.common.logging.Pluralizer;

import java.util.function.Consumer;

public class MeasureProperty<
        MeasureT extends Measure<UnitT>,
        MutMeasureT extends MutableMeasure<UnitT, MeasureT, MutMeasureT>,
        UnitT extends Unit
        > extends Property {
    final MeasureT defaultValue;
    final UnitT defaultUnit;
    final MutMeasureT lastValue;
    final MutMeasureT currentValue;

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue.mut_replace(table.get(suffix, defaultValue));
        }
    };

    public MeasureProperty(String prefix, String name, MeasureT defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public MeasureProperty(String prefix, String name, MeasureT defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name  + "-in-" + Pluralizer.pluralize(defaultValue.unit().name()), manager, level);
        this.defaultValue = defaultValue;
        this.defaultUnit = defaultValue.unit();

        currentValue = (MutMeasureT) defaultValue.mutableCopy();
        lastValue = (MutMeasureT) defaultValue.mutableCopy();


        // Check for non-default on load; also store a "last value" we can use
        // to check if a property has changed recently.
        MeasureT firstValue = get_internal();
        if (!firstValue.isEquivalent(defaultValue)) {
            log.info("Property " + key + " has the non-default value " + firstValue);
        }
        lastValue.mut_replace(firstValue);
        currentValue.mut_replace((MeasureT) firstValue.copy());
    }

    public MeasureT get() {
        return currentValue.copy();
    }


    public MeasureT get_internal() {
        Double nullableTableValue = activeStore.getDouble(key);

        if(nullableTableValue == null) {
            //log.error("Property key \"" + key + "\" not present in the underlying store!"
            //        + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            set(defaultValue);
            return defaultValue;
        }

        return (MeasureT) defaultUnit.of(nullableTableValue);
    }

    public void set(MeasureT value) {
        activeStore.setDouble(key, value.in(defaultUnit));
        currentValue.mut_replace(value);
    }

    public void hasChangedSinceLastCheck(Consumer<MeasureT> callback) {
        MeasureT currentValue = get();
        if (!currentValue.isEquivalent(lastValue)) {
            callback.accept(currentValue);
        }
        lastValue.mut_replace(currentValue);
    }

    public boolean hasChangedSinceLastCheck() {
        MeasureT currentValue = get();
        boolean changed = !currentValue.isEquivalent(lastValue);
        lastValue.mut_replace(currentValue);
        return changed;
    }

    public boolean isSetToDefault() {
        return get() == defaultValue;
    }

    @Override
    public void refreshDataFrame() {
        currentValue.mut_replace(get_internal());
        Logger.processInputs(prefix, inputs);
    }
}
