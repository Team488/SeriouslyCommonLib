package xbot.common.injection.electrical_contract;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;
import xbot.common.injection.swerve.SwerveInstance;

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
     * Returns the offset from the center of the robot as a {@link Translation2d} for the given {@link SwerveInstance}.
     * @param swerveInstance The swerve instance.
     * @return The offset from the center of the robot as a {@link Translation2d} for the given {@link SwerveInstance}, in inches.
     */
    Translation2d getSwerveModuleOffsets(SwerveInstance swerveInstance);

    /**
     * Returns the diameter of the drive wheels.
     * @return The diameter of the drive wheels.
     */
    default Distance getDriveWheelDiameter() {
        return Inches.of(4);
    }

    /**
     * Returns the gear ratio for the drive motors.
     * @return The gear ratio for the drive motors.
     */
    double getDriveGearRatio();

    /**
     * Returns the gear ratio for the steering motors.
     * @return The gear ratio for the steering motors.
     */
    double getSteeringGearRatio();
}
