package xbot.common.properties;

import edu.wpi.first.units.AngleUnit;
import edu.wpi.first.units.measure.Angle;

/**
 * This manages an Angle in the property system.
 *
 * @author Alex
 */
public class AngleProperty extends MeasureProperty<Angle, AngleUnit> {
    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager) {
        super(prefix, name, defaultValue, manager);
    }

    public AngleProperty(String prefix, String name, Angle defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, defaultValue, manager, level);
    }
}
