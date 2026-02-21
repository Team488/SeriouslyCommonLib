package xbot.common.properties;

import java.util.function.Consumer;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.MutAngle;
import xbot.common.logging.Pluralizer;

/**
 * This manages an Angle in the property system.
 *
 * @author Alex
 */
public class AngleProperty extends MeasureProperty<Angle, MutAngle, AngleUnit> {
    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager) {
        super(prefix, name, defaultValue, manager);
    }

    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, defaultValue, manager, level);
    }
}
