package xbot.common.controls.io_inputs;

import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.Time;
import org.littletonrobotics.junction.AutoLog;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Seconds;

@AutoLog
public class LaserCANInputs {
    public boolean isMeasurementValid = false;
    public Distance distance = Meters.zero();
    public Time measurementLatency = Seconds.zero();
}
