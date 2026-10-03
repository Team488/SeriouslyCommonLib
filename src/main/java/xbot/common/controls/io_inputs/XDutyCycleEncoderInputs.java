package xbot.common.controls.io_inputs;

import org.littletonrobotics.junction.AutoLog;

import org.wpilib.units.measure.Angle;

import static org.wpilib.units.Units.Rotations;

@AutoLog
public class XDutyCycleEncoderInputs {
    public Angle absoluteRawPosition = Rotations.zero();
}
