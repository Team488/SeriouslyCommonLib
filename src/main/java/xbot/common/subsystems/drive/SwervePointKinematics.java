package xbot.common.subsystems.drive;

// Contains a set of kinematics values
public class SwervePointKinematics {
    final double acceleration; //  Units: m/s^2
    final double initialVelocity; // Units: m/s
    final double goalVelocity; // m/s, velocity you want to be when you reach your goal, may not always be fulfilled.
    final double maxVelocity; // m/s

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
