package xbot.common.math.kinematics;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.MathSharedStore;
import edu.wpi.first.math.MathUsageId;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.Kinematics;
import edu.wpi.first.units.measure.Distance;

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
        implements Kinematics<DeadwheelWheelSpeeds, DeadwheelWheelPositions> {
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
     * @param trackWidth The track width of the drivetrain. Theoretically, this is
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
    public ChassisSpeeds toChassisSpeeds(DeadwheelWheelSpeeds wheelSpeeds) {
        var vx = (wheelSpeeds.frontMetersPerSecond + wheelSpeeds.rearMetersPerSecond) / 2.0;
        var vy =  (wheelSpeeds.leftMetersPerSecond + wheelSpeeds.rightMetersPerSecond) / 2.0;
        return new ChassisSpeeds(
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
    public DeadwheelWheelSpeeds toWheelSpeeds(ChassisSpeeds chassisSpeeds) {
        return new DeadwheelWheelSpeeds(
                chassisSpeeds.vxMetersPerSecond
                        - robotWidthMeters / 2 * chassisSpeeds.omegaRadiansPerSecond,
                chassisSpeeds.vxMetersPerSecond
                        + robotWidthMeters / 2 * chassisSpeeds.omegaRadiansPerSecond,
                chassisSpeeds.vyMetersPerSecond
                        - robotWidthMeters / 2 * chassisSpeeds.omegaRadiansPerSecond,
                chassisSpeeds.vyMetersPerSecond
                        - robotWidthMeters / 2 * chassisSpeeds.omegaRadiansPerSecond);
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
