package xbot.common.controls.io_inputs;

import org.littletonrobotics.junction.AutoLog;

@AutoLog
public class XCANSparkMaxInputs
{
    public boolean stickyFaultHasReset;
    public long lastErrorId;
    public double velocity;
    public double position;
    public double appliedOutput;
    public double busVoltage;
    public double outputCurrent;
}
