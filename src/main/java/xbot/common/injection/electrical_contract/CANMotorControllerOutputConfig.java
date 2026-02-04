package xbot.common.injection.electrical_contract;

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

    /**
     * Sets the inversion type for the motor controller.
     */
    public CANMotorControllerOutputConfig withInversionType(InversionType inversionType) {
        this.inversionType = inversionType;
        return this;
    }

    /**
     * Sets the neutral mode for the motor controller.
     */
    public CANMotorControllerOutputConfig withNeutralMode(NeutralMode neutralMode) {
        this.neutralMode = neutralMode;
        return this;
    }
}
