package xbot.common.injection.electrical_contract;

import edu.wpi.first.units.measure.Distance;
import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.math.XYPair;

import static edu.wpi.first.units.Units.Inches;

/**
 * This interface defines the base electrical contract
 * for robots implementing swerve drive.
 */
public interface XSwerveDriveElectricalContract {
    /**
     * Returns true if the drive is ready to be used.
     * @return True if the drive is ready to be used.
     */
    boolean isDriveReady();

    /**
     * Returns true if the steering encoders are ready to be used.
     * @return True if the steering encoders are ready to be used.
     */
    boolean areCanCodersReady();

    /**
     * Returns the {@link DeviceInfo} for the drive motor for the given {@link SwerveInstance}.
     * @param swerveInstance The swerve instance.
     * @return The {@link DeviceInfo} for the drive motor for the given {@link SwerveInstance}.
     */
    CANMotorControllerInfo getDriveMotor(SwerveInstance swerveInstance);

    /**
     * Returns the {@link DeviceInfo} for the steering  motor for the given {@link SwerveInstance}.
     * @param swerveInstance The swerve instance.
     * @return The {@link DeviceInfo} for the steering  motor for the given {@link SwerveInstance}.
     */
    CANMotorControllerInfo getSteeringMotor(SwerveInstance swerveInstance);

    /**
     * Returns the {@link DeviceInfo} for the steering encoder for the given {@link SwerveInstance}.
     * @param swerveInstance The swerve instance.
     * @return The {@link DeviceInfo} for the steering encoder for the given {@link SwerveInstance}.
     */
    DeviceInfo getSteeringEncoder(SwerveInstance swerveInstance);

    /**
     * Returns the offset from the center of the robot as an {@link XYPair} for the given {@link SwerveInstance}.
     * @param swerveInstance The swerve instance.
     * @return The offset from the center of the robot as an {@link XYPair} for the given {@link SwerveInstance}, in inches.
     */
    XYPair getSwerveModuleOffsetsInInches(SwerveInstance swerveInstance);

    /**
     * Returns the diameter of the drive wheels.
     * @return The diameter of the drive wheels.
     */
    default Distance getDriveWheelDiameter() {
        return Inches.of(2);
    }

    /**
     * Returns the gear ratio for the drive motors.
     * @return The gear ratio for the drive motors.
     */
    default double getDriveGearRatio() {
        return 6.48; // Documented value for WCP x2i with X3 10t gears.
    }

    /**
     * Returns the gear ratio for the steering motors.
     * @return The gear ratio for the steering motors.
     */
    default double getSteeringGearRatio() {
        return 12.1; // Documented value for WCP x2i.
    }
}
