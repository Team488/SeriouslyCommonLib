package xbot.common.controls.io_inputs;

import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.littletonrobotics.junction.AutoLog;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;

@AutoLog
public class XGyroIoInputs {
    public Angle yaw = Degrees.zero();
    public AngularVelocity yawAngularVelocity = DegreesPerSecond.zero();
    public Angle pitch = Degrees.zero();
    public Angle roll = Degrees.zero();
    public double[] acceleration = new double[3];
    public boolean isConnected = false;
}
