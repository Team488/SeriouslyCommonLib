package xbot.common.controls.io_inputs;

import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.littletonrobotics.junction.AutoLog;

@AutoLog
public class XAbsoluteEncoderInputs {
    public Angle position;
    public Angle absolutePosition;
    public AngularVelocity velocity;
    public String deviceHealth;
}
