package xbot.common.controls.io_inputs;

import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.LinearAcceleration;
import org.littletonrobotics.junction.AutoLog;

import static org.wpilib.units.Units.Radians;
import static org.wpilib.units.Units.RadiansPerSecond;

@AutoLog
public class XGyroIoInputs {
    public Angle yaw = Radians.zero();
    public AngularVelocity yawAngularVelocity = RadiansPerSecond.zero();
    public Angle pitch = Radians.zero();
    public Angle roll = Radians.zero();
    public LinearAcceleration[] acceleration = new LinearAcceleration[3];
    public boolean isConnected = false;
}
