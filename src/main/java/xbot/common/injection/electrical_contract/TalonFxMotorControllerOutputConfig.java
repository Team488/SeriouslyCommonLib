package xbot.common.injection.electrical_contract;

import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Time;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.Seconds;

/**
 * Configuration class for Talon FX motor controllers.
 */
public class TalonFxMotorControllerOutputConfig extends CANMotorControllerOutputConfig {

    public Current statorCurrentLimit = Amps.of(80);

    public Current supplyCurrentLimit = Amps.of(40);

    public Current burstSupplyCurrentLimit = Amps.of(70);

    public Time supplyCurrentBurstDuration = Seconds.of(1.0);

    public int feedbackCanCoderDeviceId = -1;

    @Override
    public TalonFxMotorControllerOutputConfig withInversionType(InversionType inversionType) {
        return (TalonFxMotorControllerOutputConfig) super.withInversionType(inversionType);
    }

    @Override
    public TalonFxMotorControllerOutputConfig withNeutralMode(NeutralMode neutralMode) {
        return (TalonFxMotorControllerOutputConfig) super.withNeutralMode(neutralMode);
    }

    /**
     * Sets the continuous current limit for the motor controller's stator current.
     */
    public TalonFxMotorControllerOutputConfig withStatorCurrentLimit(Current statorCurrentLimit) {
        this.statorCurrentLimit = statorCurrentLimit;
        return this;
    }

    /**
     * Sets the continuous current limit for the motor controller's supply current.
     */
    public TalonFxMotorControllerOutputConfig withSupplyCurrentLimit(Current continuousSupplyCurrentLimit,
                                                                 Current burstSupplyCurrentLimit,
                                                                 Time supplyCurrentBurstDuration) {
        this.supplyCurrentLimit = continuousSupplyCurrentLimit;
        this.burstSupplyCurrentLimit = burstSupplyCurrentLimit;
        this.supplyCurrentBurstDuration = supplyCurrentBurstDuration;
        return this;
    }

    /**
     * Sets a CANCoder as the position source for the motor
     * @param canCoderDeviceId The CAN ID of the CANCoder to use as a remote sensor.
     * @apiNote The CANCoder must be on the same bus as the motor controller.
     */
    public TalonFxMotorControllerOutputConfig withRemoteCanCoderFeedback(int canCoderDeviceId) {
        this.feedbackCanCoderDeviceId = canCoderDeviceId;
        return this;
    }
}
