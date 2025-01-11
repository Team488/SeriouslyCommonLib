package xbot.common.injection.electrical_contract;

import edu.wpi.first.units.measure.Current;

import static edu.wpi.first.units.Units.Amps;

public class CANMotorControllerOutputConfig {
    public enum InversionType {
        Normal,
        Inverted
    }

    public enum NeutralMode {
        Brake,
        Coast
    }

    public InversionType inversionType = InversionType.Normal;

    public NeutralMode neutralMode = NeutralMode.Coast;

    public Current statorCurrentLimit = Amps.of(80);

    public CANMotorControllerOutputConfig withInversionType(InversionType inversionType) {
        this.inversionType = inversionType;
        return this;
    }

    public CANMotorControllerOutputConfig withNeutralMode(NeutralMode neutralMode) {
        this.neutralMode = neutralMode;
        return this;
    }

    public CANMotorControllerOutputConfig withStatorCurrentLimit(Current statorCurrentLimit) {
        this.statorCurrentLimit = statorCurrentLimit;
        return this;
    }
}
