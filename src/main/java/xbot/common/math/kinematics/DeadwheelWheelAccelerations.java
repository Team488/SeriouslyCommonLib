package xbot.common.math.kinematics;

/** Represents the wheel accelerations for deadwheels. */
public class DeadwheelWheelAccelerations {
    /** Acceleration of the left side of the robot. */
    public double leftMetersPerSecondSquared;

    /** Acceleration of the right side of the robot. */
    public double rightMetersPerSecondSquared;

    /** Acceleration of the front side of the robot. */
    public double frontMetersPerSecondSquared;

    /** Acceleration of the rear side of the robot. */
    public double rearMetersPerSecondSquared;

    /** Constructs a DeadwheelWheelAccelerations with zeros for all accelerations. */
    public DeadwheelWheelAccelerations() {
    }

    /**
     * Constructs a DeadwheelWheelAccelerations.
     *
     * @param leftMetersPerSecondSquared  The left acceleration.
     * @param rightMetersPerSecondSquared The right acceleration.
     * @param frontMetersPerSecondSquared The front acceleration.
     * @param rearMetersPerSecondSquared  The rear acceleration.
     */
    public DeadwheelWheelAccelerations(double leftMetersPerSecondSquared, double rightMetersPerSecondSquared,
            double frontMetersPerSecondSquared, double rearMetersPerSecondSquared) {
        this.leftMetersPerSecondSquared = leftMetersPerSecondSquared;
        this.rightMetersPerSecondSquared = rightMetersPerSecondSquared;
        this.frontMetersPerSecondSquared = frontMetersPerSecondSquared;
        this.rearMetersPerSecondSquared = rearMetersPerSecondSquared;
    }
}
