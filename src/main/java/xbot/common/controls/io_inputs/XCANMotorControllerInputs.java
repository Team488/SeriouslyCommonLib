package xbot.common.controls.io_inputs;

import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.Current;
import org.wpilib.units.measure.Voltage;
import org.littletonrobotics.junction.AutoLog;

import static org.wpilib.units.Units.Amps;
import static org.wpilib.units.Units.RPM;
import static org.wpilib.units.Units.Rotations;
import static org.wpilib.units.Units.Volts;

@AutoLog
public class XCANMotorControllerInputs {
    public Angle angle = Rotations.zero();
    public AngularVelocity angularVelocity = RPM.zero();
    public Voltage voltage = Volts.zero();
    public Current current = Amps.zero();
}
