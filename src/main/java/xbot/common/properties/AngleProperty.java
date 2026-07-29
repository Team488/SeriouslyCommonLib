package xbot.common.properties;

import org.wpilib.units.AngleUnit;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.MutAngle;

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
