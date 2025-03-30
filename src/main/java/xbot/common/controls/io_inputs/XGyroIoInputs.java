package xbot.common.controls.io_inputs;

import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import org.littletonrobotics.junction.AutoLog;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

@AutoLog
public class XGyroIoInputs {
    public Angle yaw = Degrees.zero();
    public AngularVelocity yawAngularVelocity = DegreesPerSecond.zero();
    public Angle pitch = Degrees.zero();
    public Angle roll = Degrees.zero();
    public double[] acceleration = new double[3];
    public boolean isConnected = false;
}
