package xbot.common.subsystems.drive.swerve;

import edu.wpi.first.math.kinematics.SwerveModuleState;

/**
 * A simple record to hold the states of all four swerve modules.
 */
public record SwerveModuleStates(SwerveModuleState frontLeft,
                                 SwerveModuleState frontRight,
                                 SwerveModuleState rearLeft,
                                 SwerveModuleState rearRight) {
    public SwerveModuleState[] toArray() {
        return new SwerveModuleState[] {
            frontLeft,
            frontRight,
            rearLeft,
            rearRight
        };
    }
}
