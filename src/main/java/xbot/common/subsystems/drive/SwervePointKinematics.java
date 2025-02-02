package xbot.common.subsystems.drive;

import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.LinearVelocity;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.MetersPerSecondPerSecond;

public record SwervePointKinematics(LinearAcceleration acceleration, LinearVelocity initialVelocity, LinearVelocity goalVelocity, LinearVelocity maxVelocity) {
    /**
     * Creates a set of Kinematics values with METERS
     * @param acceleration in meters/second^2
     * @param initialVelocity in meters/second; velocity at start
     * @param goalVelocity in meters/second; velocity we WANT to end at, in range of [-maxVelocity, maxVelocity]
     * @param maxVelocity in meters/second; max speed constraint
     */
    public SwervePointKinematics(double acceleration, double initialVelocity, double goalVelocity, double maxVelocity) {
        this(MetersPerSecondPerSecond.of(acceleration), MetersPerSecond.of(initialVelocity), MetersPerSecond.of(Math.max(Math.min(maxVelocity, goalVelocity),
                -maxVelocity)), MetersPerSecond.of(maxVelocity));
    }

    /**
     * @param newInitialVelocity for the set of kinematics
     * @return a new set of kinematics values!
     */
    public SwervePointKinematics kinematicsWithNewInitialVelocity(LinearVelocity newInitialVelocity) {
        return new SwervePointKinematics(acceleration, newInitialVelocity, goalVelocity, maxVelocity);
    }
}
