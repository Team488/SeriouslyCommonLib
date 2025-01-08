package xbot.common.subsystems.drive;

// Contains a set of kinematics values
public class SwervePointKinematics {
    final double acceleration; // Acceleration
    final double initialVelocity; // Initial velocity
    final double goalVelocity; // Goal velocity/Velocity at goal (may not 100% fulfilled)
    final double maxVelocity; // Max velocity

    public SwervePointKinematics() {
        this(0, 0, 0, 0);
    }

    public SwervePointKinematics(double a, double initialVelocity, double goalVelocity, double maxVelocity) {
        this.acceleration = a;
        this.initialVelocity = initialVelocity;
        this.goalVelocity = Math.max(Math.min(maxVelocity, goalVelocity), -maxVelocity);
        this.maxVelocity = maxVelocity;
    }

    public SwervePointKinematics kinematicsWithNewVi(double newVi) {
        return new SwervePointKinematics(acceleration, newVi, goalVelocity, maxVelocity);
    }

    public double getAcceleration() {
        return this.acceleration;
    }

    public double getMaxVelocity() {
        return this.maxVelocity;
    }
}
