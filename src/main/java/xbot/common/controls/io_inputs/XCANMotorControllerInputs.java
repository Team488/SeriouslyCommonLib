package xbot.common.controls.io_inputs;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Voltage;
import org.littletonrobotics.junction.AutoLog;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.Volts;

@AutoLog
public class XCANMotorControllerInputs {
    public Angle angle = Rotations.zero();
    public AngularVelocity angularVelocity = RPM.zero();
    public Voltage voltage = Volts.zero();
    public Current current = Amps.zero();
}
