package xbot.common.properties;

import edu.wpi.first.units.Measure;
import edu.wpi.first.units.MutableMeasure;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import xbot.common.logging.Pluralizer;

import java.util.function.Consumer;

public class MeasureProperty<
        ImmutMeasure extends Measure<Unit>,
        MutMeasure extends MutableMeasure<Unit, ImmutMeasure, MutMeasure>,
        Unit extends edu.wpi.first.units.Unit
        > extends Property {
    final ImmutMeasure defaultValue;
    final Unit defaultUnit;
    final MutMeasure lastValue;
    final MutMeasure currentValue;

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue.mut_replace(table.get(suffix, defaultValue));
        }
    };

    public MeasureProperty(String prefix, String name, ImmutMeasure defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public MeasureProperty(String prefix, String name, ImmutMeasure defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name  + "-in-" + Pluralizer.pluralize(defaultValue.unit().name()), manager, level);
        this.defaultValue = defaultValue;
        this.defaultUnit = defaultValue.unit();

        currentValue = (MutMeasure) defaultValue.mutableCopy();
        lastValue = (MutMeasure) defaultValue.mutableCopy();


        // Check for non-default on load; also store a "last value" we can use
        // to check if a property has changed recently.
        ImmutMeasure firstValue = get_internal();
        if (!firstValue.isEquivalent(defaultValue)) {
            log.info("Property " + key + " has the non-default value " + firstValue);
        }
        lastValue.mut_replace(firstValue);
        currentValue.mut_replace((ImmutMeasure) firstValue.copy());
    }

    public ImmutMeasure get() {
        return currentValue.copy();
    }


    public ImmutMeasure get_internal() {
        Double nullableTableValue = activeStore.getDouble(key);

        if(nullableTableValue == null) {
            //log.error("Property key \"" + key + "\" not present in the underlying store!"
            //        + " IF THIS IS AN IMPORTANT ROBOT PROPERTY, MAKE SURE IT HAS A SANE VALUE BEFORE ENABLING THE ROBOT!");
            set(defaultValue);
            return defaultValue;
        }

        return (ImmutMeasure) defaultUnit.of(nullableTableValue);
    }

    public void set(ImmutMeasure value) {
        activeStore.setDouble(key, value.in(defaultUnit));
        currentValue.mut_replace(value);
    }

    public void hasChangedSinceLastCheck(Consumer<ImmutMeasure> callback) {
        ImmutMeasure currentValue = get();
        if (!currentValue.isEquivalent(lastValue)) {
            callback.accept(currentValue);
        }
        lastValue.mut_replace(currentValue);
    }

    public boolean hasChangedSinceLastCheck() {
        ImmutMeasure currentValue = get();
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
