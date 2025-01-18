package xbot.common.controls.io_inputs;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import org.littletonrobotics.junction.AutoLog;

@AutoLog
public class XCANMotorControllerInputs {
    public Angle angle;
    public AngularVelocity angularVelocity;
    public Voltage voltage;
    public Current current;
}
