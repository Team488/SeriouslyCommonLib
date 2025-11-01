package xbot.common.subsystems.drive.swerve;

import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * A simple record to hold the states of all four swerve modules.
 */
public record SwerveModuleStates(SwerveModuleState frontLeft,
                                 SwerveModuleState frontRight,
                                 SwerveModuleState rearLeft,
                                 SwerveModuleState rearRight) {
    /**
     * Get the state of the front left swerve module.
     * @return The front left swerve module state.
     */
    public SwerveModuleState getFrontLeft() {
        return frontLeft;
    }

    /**
     * Get the state of the front right swerve module.
     * @return The front right swerve module state.
     */
    public SwerveModuleState getFrontRight() {
        return frontRight;
    }

    /**
     * Get the state of the back left swerve module.
     * @return The back left swerve module state.
     */
    public SwerveModuleState getRearLeft() {
        return rearLeft;
    }

    /**
     * Get the state of the back right swerve module.
     * @return The back right swerve module state.
     */
    public SwerveModuleState getRearRight() {
        return rearRight;
    }

    public SwerveModuleState[] toArray() {
        return new SwerveModuleState[] {
            frontLeft,
            frontRight,
            rearLeft,
            rearRight
        };
    }
}
