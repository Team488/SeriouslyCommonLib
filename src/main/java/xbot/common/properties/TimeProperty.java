package xbot.common.properties;

import org.wpilib.units.TimeUnit;
import org.wpilib.units.measure.MutTime;
import org.wpilib.units.measure.Time;

/**
 * This manages an Angle in the property system.
 *
 * @author Alex
 */
public class TimeProperty extends MeasureProperty<Time, MutTime, TimeUnit> {
    public TimeProperty(String prefix, String name, Time defaultValue, XPropertyManager manager) {
        super(prefix, name, defaultValue, manager);
    }

    public TimeProperty(String prefix, String name, Time defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, defaultValue, manager, level);
    }
}
