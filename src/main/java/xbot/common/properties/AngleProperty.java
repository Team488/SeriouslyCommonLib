package xbot.common.properties;

import static edu.wpi.first.units.Units.Degrees;

import java.util.function.Consumer;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.MutAngle;
import xbot.common.logging.Pluralizer;

/**
 * This manages an Angle in the property system.
 *
 * @author Alex
 */
public class AngleProperty extends Property {
    final Angle defaultValue;
    final AngleUnit defaultUnit;
    final MutAngle lastValue;
    final MutAngle currentValue;
    final static Angle equivalenceThreshold = Degrees.of(0.001);

    private final LoggableInputs inputs = new LoggableInputs() {
        public void toLog(LogTable table) {
            table.put(suffix, currentValue);
        }

        public void fromLog(LogTable table) {
            currentValue.mut_replace(table.get(suffix, defaultValue));
        }
    };

    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager) {
        this(prefix, name, defaultValue, manager, PropertyLevel.Important);
    }

    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name  + "-in-" + Pluralizer.pluralize(defaultValue.unit().name()), manager, level);
        this.defaultValue = defaultValue;
        this.defaultUnit = defaultValue.unit();

        currentValue = defaultValue.mutableCopy();
        lastValue = defaultValue.mutableCopy();


        // Check for non-default on load; also store a "last value" we can use
        // to check if a property has changed recently.
        Angle firstValue = get_internal();
        if (firstValue != defaultValue) {
            log.info("Property " + key + " has the non-default value " + firstValue);
        }
        lastValue.mut_replace(firstValue);
        currentValue.mut_replace(firstValue.mutableCopy());
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
        currentValue.mut_replace(value);
    }

    public void hasChangedSinceLastCheck(Consumer<Angle> callback) {
        Angle currentValue = get();
        if (!currentValue.isNear(lastValue, equivalenceThreshold)) {
            callback.accept(currentValue);
        }
        lastValue.mut_replace(currentValue);
    }

    public boolean hasChangedSinceLastCheck() {
        Angle currentValue = get();
        boolean changed = !currentValue.isNear(lastValue, equivalenceThreshold);
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
