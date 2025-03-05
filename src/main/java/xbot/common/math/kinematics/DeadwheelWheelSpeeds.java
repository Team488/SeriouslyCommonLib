package xbot.common.math.kinematics;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.units.measure.LinearVelocity;

/** Represents the wheel speeds for deadwheels. */
public class DeadwheelWheelSpeeds {
    /** Speed of the left side of the robot. */
    public double leftMetersPerSecond;

    /** Speed of the right side of the robot. */
    public double rightMetersPerSecond;

    /** Speed of the right side of the robot. */
    public double frontMetersPerSecond;

    /** Speed of the right side of the robot. */
    public double rearMetersPerSecond;

    /** Constructs a DeadwheelWheelSpeeds with zeros for left and right speeds. */
    public DeadwheelWheelSpeeds() {
    }

    /**
     * Constructs a DifferentialDriveWheelSpeeds.
     *
     * @param leftMetersPerSecond  The left speed.
     * @param rightMetersPerSecond The right speed.
     * @param frontMetersPerSecond The front speed.
     * @param rearMetersPerSecond  The rear speed.
     */
    public DeadwheelWheelSpeeds(double leftMetersPerSecond, double rightMetersPerSecond, double frontMetersPerSecond,
            double rearMetersPerSecond) {
        this.leftMetersPerSecond = leftMetersPerSecond;
        this.rightMetersPerSecond = rightMetersPerSecond;
        this.frontMetersPerSecond = frontMetersPerSecond;
        this.rearMetersPerSecond = rearMetersPerSecond;
    }

    /**
     * Renormalizes the wheel speeds if any either side is above the specified
     * maximum.
     *
     * <p>
     * Sometimes, after inverse kinematics, the requested speed from one or more
     * wheels may be
     * above the max attainable speed for the driving motor on that wheel. To fix
     * this issue, one can
     * reduce all the wheel speeds to make sure that all requested module speeds are
     * at-or-below the
     * absolute threshold, while maintaining the ratio of speeds between wheels.
     *
     * @param attainableMaxSpeedMetersPerSecond The absolute max speed that a wheel
     *                                          can reach.
     */
    public void desaturate(double attainableMaxSpeedMetersPerSecond) {
        double realMaxSpeed = Math.max(Math.abs(leftMetersPerSecond), Math.abs(rightMetersPerSecond));

        if (realMaxSpeed > attainableMaxSpeedMetersPerSecond) {
            leftMetersPerSecond = leftMetersPerSecond / realMaxSpeed * attainableMaxSpeedMetersPerSecond;
            rightMetersPerSecond = rightMetersPerSecond / realMaxSpeed * attainableMaxSpeedMetersPerSecond;
            frontMetersPerSecond = frontMetersPerSecond / realMaxSpeed * attainableMaxSpeedMetersPerSecond;
            rearMetersPerSecond = rearMetersPerSecond / realMaxSpeed * attainableMaxSpeedMetersPerSecond;
        }
    }

    /**
     * Renormalizes the wheel speeds if any either side is above the specified
     * maximum.
     *
     * <p>
     * Sometimes, after inverse kinematics, the requested speed from one or more
     * wheels may be
     * above the max attainable speed for the driving motor on that wheel. To fix
     * this issue, one can
     * reduce all the wheel speeds to make sure that all requested module speeds are
     * at-or-below the
     * absolute threshold, while maintaining the ratio of speeds between wheels.
     *
     * @param attainableMaxSpeed The absolute max speed that a wheel can reach.
     */
    public void desaturate(LinearVelocity attainableMaxSpeed) {
        desaturate(attainableMaxSpeed.in(MetersPerSecond));
    }

    /**
     * Adds two DifferentialDriveWheelSpeeds and returns the sum.
     *
     * <p>
     * For example, DifferentialDriveWheelSpeeds{1.0, 0.5} +
     * DifferentialDriveWheelSpeeds{2.0, 1.5}
     * = DifferentialDriveWheelSpeeds{3.0, 2.0}
     *
     * @param other The DifferentialDriveWheelSpeeds to add.
     * @return The sum of the DifferentialDriveWheelSpeeds.
     */
    public DeadwheelWheelSpeeds plus(DeadwheelWheelSpeeds other) {
        return new DeadwheelWheelSpeeds(
                leftMetersPerSecond + other.leftMetersPerSecond,
                rightMetersPerSecond + other.rightMetersPerSecond,
                frontMetersPerSecond + other.frontMetersPerSecond,
                rearMetersPerSecond + other.rearMetersPerSecond);
    }

    /**
     * Subtracts the other DifferentialDriveWheelSpeeds from the current
     * DifferentialDriveWheelSpeeds
     * and returns the difference.
     *
     * <p>
     * For example, DifferentialDriveWheelSpeeds{5.0, 4.0} -
     * DifferentialDriveWheelSpeeds{1.0, 2.0}
     * = DifferentialDriveWheelSpeeds{4.0, 2.0}
     *
     * @param other The DifferentialDriveWheelSpeeds to subtract.
     * @return The difference between the two DifferentialDriveWheelSpeeds.
     */
    public DeadwheelWheelSpeeds minus(DeadwheelWheelSpeeds other) {
        return new DeadwheelWheelSpeeds(
                leftMetersPerSecond - other.leftMetersPerSecond,
                rightMetersPerSecond - other.rightMetersPerSecond,
                frontMetersPerSecond - other.frontMetersPerSecond,
                rearMetersPerSecond - other.rearMetersPerSecond);
    }

    /**
     * Returns the inverse of the current DifferentialDriveWheelSpeeds. This is
     * equivalent to negating
     * all components of the DifferentialDriveWheelSpeeds.
     *
     * @return The inverse of the current DifferentialDriveWheelSpeeds.
     */
    public DeadwheelWheelSpeeds unaryMinus() {
        return new DeadwheelWheelSpeeds(-leftMetersPerSecond, -rightMetersPerSecond, -frontMetersPerSecond,
                -rearMetersPerSecond);
    }

    /**
     * Multiplies the DifferentialDriveWheelSpeeds by a scalar and returns the new
     * DifferentialDriveWheelSpeeds.
     *
     * <p>
     * For example, DifferentialDriveWheelSpeeds{2.0, 2.5} * 2 =
     * DifferentialDriveWheelSpeeds{4.0,
     * 5.0}
     *
     * @param scalar The scalar to multiply by.
     * @return The scaled DifferentialDriveWheelSpeeds.
     */
    public DeadwheelWheelSpeeds times(double scalar) {
        return new DeadwheelWheelSpeeds(
                leftMetersPerSecond * scalar, rightMetersPerSecond * scalar, frontMetersPerSecond * scalar,
                rearMetersPerSecond * scalar);
    }

    /**
     * Divides the DifferentialDriveWheelSpeeds by a scalar and returns the new
     * DifferentialDriveWheelSpeeds.
     *
     * <p>
     * For example, DifferentialDriveWheelSpeeds{2.0, 2.5} / 2 =
     * DifferentialDriveWheelSpeeds{1.0,
     * 1.25}
     *
     * @param scalar The scalar to divide by.
     * @return The scaled DifferentialDriveWheelSpeeds.
     */
    public DeadwheelWheelSpeeds div(double scalar) {
        return new DeadwheelWheelSpeeds(
                leftMetersPerSecond / scalar, rightMetersPerSecond / scalar, frontMetersPerSecond / scalar,
                rearMetersPerSecond / scalar);
    }

    @Override
    public String toString() {
        return String.format(
                "DifferentialDriveWheelSpeeds(Left: %.2f m/s, Right: %.2f m/s, Front: %.2f m/s, Rear: %.2f m/s)",
                leftMetersPerSecond, rightMetersPerSecond, frontMetersPerSecond, rearMetersPerSecond);
    }
}
