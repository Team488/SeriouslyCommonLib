package xbot.common.injection.electrical_contract;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;
import edu.wpi.first.units.measure.Time;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Seconds;

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

    public Current supplyCurrentLimit = Amps.of(40);

    public Current burstSupplyCurrentLimit = Amps.of(70);

    public Current sparkStallCurrentLimit = Amps.of(20);

    public Current sparkFreeCurrentLimit = Amps.of(80);

    public AngularVelocity sparkStallSpeed = RPM.of(100);

    public Time supplyCurrentBurstDuration = Seconds.of(1.0);

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

    /**
     * Sets the continuous current limit for the motor controller's stator current.
     * @apiNote This is only supported on TalonFX motor controllers.
     */
    public CANMotorControllerOutputConfig withStatorCurrentLimit(Current statorCurrentLimit) {
        this.statorCurrentLimit = statorCurrentLimit;
        return this;
    }

    /**
     * Sets the continuous current limit for the motor controller's supply current.
     * @apiNote This is only supported on TalonFX motor controllers.
     */
    public CANMotorControllerOutputConfig withSupplyCurrentLimit(Current continuousSupplyCurrentLimit,
                                                                 Current burstSupplyCurrentLimit,
                                                                 Time supplyCurrentBurstDuration) {
        this.supplyCurrentLimit = continuousSupplyCurrentLimit;
        this.burstSupplyCurrentLimit = burstSupplyCurrentLimit;
        this.supplyCurrentBurstDuration = supplyCurrentBurstDuration;
        return this;
    }

    /**
     * Sets the smart current limits for Spark motor controllers.
     * RPMs lower than the stallSpeed will be limited to stallCurrent,
     * @apiNote This is only supported on Spark motor controllers.
     */
    public CANMotorControllerOutputConfig withSmartCurrentLimit(Current stallCurrent, Current freeCurrent, AngularVelocity stallSpeed) {
        this.sparkStallCurrentLimit = stallCurrent;
        this.sparkFreeCurrentLimit = freeCurrent;
        this.sparkStallSpeed = stallSpeed;
        return this;
    }
}
