package xbot.common.controls.io_inputs;

import org.wpilib.units.measure.Distance;
import org.wpilib.units.measure.Time;
import org.littletonrobotics.junction.AutoLog;

import static org.wpilib.units.Units.Meters;
import static org.wpilib.units.Units.Seconds;

@AutoLog
public class LaserCANInputs {
    public boolean isMeasurementValid = false;
    public Distance distance = Meters.zero();
    public Time measurementLatency = Seconds.zero();
}
