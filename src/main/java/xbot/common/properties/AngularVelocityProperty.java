package xbot.common.properties;

import edu.wpi.first.units.AngularVelocityUnit;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.MutAngularVelocity;
import org.littletonrobotics.junction.LogTable;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.inputs.LoggableInputs;
import xbot.common.logging.Pluralizer;

import java.util.function.Consumer;

/**
 * This manages an AngleVelocity in the property system.
 */
public class AngularVelocityProperty extends MeasureProperty<AngularVelocity, MutAngularVelocity, AngularVelocityUnit> {
    public AngularVelocityProperty(String prefix, String name, AngularVelocity defaultValue, XPropertyManager manager) {
        super(prefix, name, defaultValue, manager);
    }

    public AngularVelocityProperty(String prefix, String name, AngularVelocity defaultValue, XPropertyManager manager, PropertyLevel level) {
        super(prefix, name, defaultValue, manager, level);
    }
}
