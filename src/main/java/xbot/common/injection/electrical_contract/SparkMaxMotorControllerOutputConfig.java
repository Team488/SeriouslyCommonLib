package xbot.common.injection.electrical_contract;

import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Current;

import static edu.wpi.first.units.Units.Amps;
import static edu.wpi.first.units.Units.RPM;

/**
 * Configuration class for Spark Max motor controllers.
 */
public class SparkMaxMotorControllerOutputConfig extends CANMotorControllerOutputConfig {

    public Current sparkStallCurrentLimit = Amps.of(20);

    public Current sparkFreeCurrentLimit = Amps.of(80);

    public AngularVelocity sparkStallSpeed = RPM.of(100);

    @Override
    public SparkMaxMotorControllerOutputConfig withInversionType(InversionType inversionType) {
        return (SparkMaxMotorControllerOutputConfig) super.withInversionType(inversionType);
    }

    @Override
    public SparkMaxMotorControllerOutputConfig withNeutralMode(NeutralMode neutralMode) {
        return (SparkMaxMotorControllerOutputConfig) super.withNeutralMode(neutralMode);
    }

    /**
     * Sets the smart current limits for Spark motor controllers.
     * RPMs lower than the stallSpeed will be limited to stallCurrent,
     * RPMs higher than the stall speed with have current limits linearly increasing
     * with speed (up to the max free speed of the motor) up to freeCurrent..
     */
    public SparkMaxMotorControllerOutputConfig withSmartCurrentLimit(Current stallCurrent, Current freeCurrent, AngularVelocity stallSpeed) {
        this.sparkStallCurrentLimit = stallCurrent;
        this.sparkFreeCurrentLimit = freeCurrent;
        this.sparkStallSpeed = stallSpeed;
        return this;
    }

    /**
     * Sets the smart current limits for Spark motor controllers, using the same current for stall and free.
     * This method sets a fixed current limit over the whole speed range.
     */
    public SparkMaxMotorControllerOutputConfig withSmartCurrentLimit(Current stallCurrent) {
        this.sparkStallCurrentLimit = stallCurrent;
        this.sparkFreeCurrentLimit = stallCurrent;
        this.sparkStallSpeed = RPM.of(0);
        return this;
    }
}
