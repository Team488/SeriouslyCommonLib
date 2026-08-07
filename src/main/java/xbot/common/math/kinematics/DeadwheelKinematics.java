package xbot.common.math.kinematics;

import static org.wpilib.units.Units.Meters;

import org.wpilib.math.geometry.Twist2d;
import org.wpilib.math.kinematics.ChassisAccelerations;
import org.wpilib.math.kinematics.ChassisVelocities;
import org.wpilib.math.kinematics.Kinematics;
import org.wpilib.units.measure.Distance;

/**
 * Helper class that converts a deadwheel velocity (dx and dtheta components) to
 * left and right wheel
 * velocities for a differential drive.
 *
 * <p>
 * Inverse kinematics converts a desired chassis speed into left and right
 * velocity components
 * whereas forward kinematics converts left and right component velocities into
 * a linear and angular
 * chassis speed.
 */
public class DeadwheelKinematics
        implements Kinematics<DeadwheelWheelPositions, DeadwheelWheelSpeeds, DeadwheelWheelAccelerations> {
    /** Differential drive trackwidth. */
    public final double robotWidthMeters;

    /**
     * Constructs a deadwheel kinematics object.
     *
     * @param robotWidthMeters The track width of the drivetrain. Theoretically,
     *                         this is the distance
     *                         between the left wheels and right wheels. However,
     *                         the empirical value may be larger than
     *                         the physical measured value due to scrubbing effects.
     */
    public DeadwheelKinematics(double robotWidthMeters) {
        this.robotWidthMeters = robotWidthMeters;
    }

    /**
     * Constructs a deadwheel kinematics object.
     *
     * @param robotWidth The track width of the drivetrain. Theoretically, this is
     *                   the distance
     *                   between the left wheels and right wheels. However, the
     *                   empirical value may be larger than
     *                   the physical measured value due to scrubbing effects.
     */
    public DeadwheelKinematics(Distance robotWidth) {
        this(robotWidth.in(Meters));
    }

    /**
     * Returns a chassis speed from left and right component velocities using
     * forward kinematics.
     *
     * @param wheelSpeeds The left and right velocities.
     * @return The chassis speed.
     */
    @Override
    public ChassisVelocities toChassisVelocities(DeadwheelWheelSpeeds wheelSpeeds) {
        var vx = (wheelSpeeds.frontMetersPerSecond + wheelSpeeds.rearMetersPerSecond) / 2.0;
        var vy =  (wheelSpeeds.leftMetersPerSecond + wheelSpeeds.rightMetersPerSecond) / 2.0;
        return new ChassisVelocities(
                vx,
                vy,
                0);
    }

    /**
     * Returns left and right component velocities from a chassis speed using
     * inverse kinematics.
     *
     * @param chassisSpeeds The linear and angular (dx and dtheta) components that
     *                      represent the
     *                      chassis' speed.
     * @return The left, right, front, rear velocities.
     */
    @Override
    public DeadwheelWheelSpeeds toWheelVelocities(ChassisVelocities chassisVelocities) {
        return new DeadwheelWheelSpeeds(
                chassisVelocities.vx
                        - robotWidthMeters / 2 * chassisVelocities.omega,
                chassisVelocities.vx
                        + robotWidthMeters / 2 * chassisVelocities.omega,
                chassisVelocities.vy
                        - robotWidthMeters / 2 * chassisVelocities.omega,
                chassisVelocities.vy
                        - robotWidthMeters / 2 * chassisVelocities.omega);
    }

    /**
     * Returns a chassis acceleration from left and right component accelerations using
     * forward kinematics. Uses the same linear relationship as {@link #toChassisVelocities},
     * since differentiating both sides of that relationship preserves it.
     *
     * @param wheelAccelerations The left, right, front, rear accelerations.
     * @return The chassis acceleration.
     */
    @Override
    public ChassisAccelerations toChassisAccelerations(DeadwheelWheelAccelerations wheelAccelerations) {
        var ax = (wheelAccelerations.frontMetersPerSecondSquared + wheelAccelerations.rearMetersPerSecondSquared) / 2.0;
        var ay = (wheelAccelerations.leftMetersPerSecondSquared + wheelAccelerations.rightMetersPerSecondSquared) / 2.0;
        return new ChassisAccelerations(ax, ay, 0);
    }

    /**
     * Returns left and right component accelerations from a chassis acceleration using
     * inverse kinematics. Uses the same linear relationship as {@link #toWheelVelocities}.
     *
     * @param chassisAccelerations The linear and angular acceleration components.
     * @return The left, right, front, rear accelerations.
     */
    @Override
    public DeadwheelWheelAccelerations toWheelAccelerations(ChassisAccelerations chassisAccelerations) {
        return new DeadwheelWheelAccelerations(
                chassisAccelerations.ax
                        - robotWidthMeters / 2 * chassisAccelerations.alpha,
                chassisAccelerations.ax
                        + robotWidthMeters / 2 * chassisAccelerations.alpha,
                chassisAccelerations.ay
                        - robotWidthMeters / 2 * chassisAccelerations.alpha,
                chassisAccelerations.ay
                        - robotWidthMeters / 2 * chassisAccelerations.alpha);
    }

    @Override
    public Twist2d toTwist2d(
            DeadwheelWheelPositions start, DeadwheelWheelPositions end) {
        return toTwist2d(end.leftMeters - start.leftMeters, end.rightMeters - start.rightMeters,
                end.frontMeters - start.frontMeters, end.rearMeters - start.rearMeters);
    }

    /**
     * Performs forward kinematics to return the resulting Twist2d from the given
     * left and right side
     * distance deltas. This method is often used for odometry -- determining the
     * robot's position on
     * the field using changes in the distance driven by each wheel on the robot.
     *
     * @param leftDistanceMeters  The distance measured by the left side encoder.
     * @param rightDistanceMeters The distance measured by the right side encoder.
     * @param frontDistanceMeters The distance measured by the front side encoder.
     * @param rearDistanceMeters  The distance measured by the rear side encoder.
     * @return The resulting Twist2d.
     */
    public Twist2d toTwist2d(double leftDistanceMeters, double rightDistanceMeters, double frontDistanceMeters,
            double rearDistanceMeters) {
        var dx = (frontDistanceMeters + rearDistanceMeters) / 2.0;
        var dy =  (leftDistanceMeters + rightDistanceMeters)/ 2.0;
        return new Twist2d(
                dx,
                dy,
                0);
    }

    @Override
    public DeadwheelWheelPositions copy(DeadwheelWheelPositions positions) {
        return new DeadwheelWheelPositions(positions.leftMeters, positions.rightMeters, positions.frontMeters,
                positions.rearMeters);
    }

    @Override
    public void copyInto(
            DeadwheelWheelPositions positions, DeadwheelWheelPositions output) {
        output.leftMeters = positions.leftMeters;
        output.rightMeters = positions.rightMeters;
        output.frontMeters = positions.frontMeters;
        output.rearMeters = positions.rearMeters;
    }

    @Override
    public DeadwheelWheelPositions interpolate(
            DeadwheelWheelPositions startValue,
            DeadwheelWheelPositions endValue,
            double t) {
        return startValue.interpolate(endValue, t);
    }
}
