package xbot.common.subsystems.drive.swerve;

import org.wpilib.math.kinematics.SwerveModuleVelocity;

/**
 * A simple record to hold the states of all four swerve modules.
 */
public record SwerveModuleStates(SwerveModuleVelocity frontLeft,
                                 SwerveModuleVelocity frontRight,
                                 SwerveModuleVelocity rearLeft,
                                 SwerveModuleVelocity rearRight) {
    public SwerveModuleVelocity[] toArray() {
        return new SwerveModuleVelocity[] {
            frontLeft,
            frontRight,
            rearLeft,
            rearRight
        };
    }
}
