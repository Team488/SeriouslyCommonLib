package xbot.common.subsystems.drive;

public record SwervePointKinematics(double acceleration, double initialVelocity, double goalVelocity, double maxVelocity) {
    /**
     * A set of kinematics values to be used
     * @param acceleration    in meters/second^2
     * @param initialVelocity in meters/second; velocity at start
     * @param goalVelocity    in meters/second; velocity we WANT to end at
     * @param maxVelocity     in meters/second; max speed constraint
     */
    public SwervePointKinematics(double acceleration, double initialVelocity, double goalVelocity, double maxVelocity) {
        this.acceleration = acceleration;
        this.initialVelocity = initialVelocity;
        this.goalVelocity = Math.max(Math.min(maxVelocity, goalVelocity), -maxVelocity);
        this.maxVelocity = maxVelocity;
    }

    /**
     * @param newInitialVelocity for the set of kinematics
     * @return a new set of kinematics values!
     */
    public SwervePointKinematics kinematicsWithNewInitialVelocity(double newInitialVelocity) {
        return new SwervePointKinematics(acceleration, newInitialVelocity, goalVelocity, maxVelocity);
    }
}
