package xbot.common.controls.io_inputs;

import edu.wpi.first.math.Vector;
import edu.wpi.first.math.numbers.N3;
import org.littletonrobotics.junction.AutoLog;

@AutoLog
public class XGyroIoInputs {
    public double yaw;
    public double yawAngularVelocity;
    public double pitch;
    public double roll;
    public double[] acceleration;
    public boolean isConnected;
}
