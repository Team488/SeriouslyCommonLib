package xbot.common.properties;

import org.wpilib.units.DistanceUnit;
import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.MutDistance;

/**
 * This manages a Distance in the property system.
 *
 * @author Alex
 */
public class DistanceProperty extends MeasureProperty<Distance, MutDistance, DistanceUnit> {
    public DistanceProperty(String prefix, String name, Distance defaultValue, XPropertyManager manager) {
        super(prefix, name, defaultValue, manager);
    }

    public DistanceProperty(String prefix, String name, Distance defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, defaultValue, manager, level);
    }
}
