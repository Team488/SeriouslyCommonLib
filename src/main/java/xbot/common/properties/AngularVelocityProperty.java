package xbot.common.properties;

import org.wpilib.units.AngularVelocityUnit;
import org.wpilib.units.measure.AngularVelocity;

/**
 * This manages an AngleVelocity in the property system.
 */
public class AngularVelocityProperty extends MeasureProperty<AngularVelocity, AngularVelocityUnit> {
    public AngularVelocityProperty(String prefix, String name, AngularVelocity defaultValue, XPropertyManager manager) {
        super(prefix, name, defaultValue, manager);
    }

    public AngularVelocityProperty(String prefix, String name, AngularVelocity defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, defaultValue, manager, level);
    }
}
