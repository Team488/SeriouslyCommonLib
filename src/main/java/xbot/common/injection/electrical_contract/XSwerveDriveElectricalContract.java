package xbot.common.injection.electrical_contract;

import xbot.common.injection.swerve.SwerveInstance;
import xbot.common.math.XYPair;

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
    DeviceInfo getDriveMotor(SwerveInstance swerveInstance);

    /**
     * Returns the {@link DeviceInfo} for the steering  motor for the given {@link SwerveInstance}.
     * @param swerveInstance The swerve instance.
     * @return The {@link DeviceInfo} for the steering  motor for the given {@link SwerveInstance}.
     */
    DeviceInfo getSteeringMotor(SwerveInstance swerveInstance);

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
    XYPair getSwerveModuleOffsets(SwerveInstance swerveInstance);
}
