package xbot.common.injection.electrical_contract;

import edu.wpi.first.units.measure.Current;

public class CANMotorControllerOutputConfig {
    public enum InversionType {
        Normal,
        Inverted
    }

    public enum NeutralMode {
        Brake,
        Coast
    }

    public InversionType inversionType;

    public NeutralMode neutralMode;

    public Current statorCurrentLimit;
}
