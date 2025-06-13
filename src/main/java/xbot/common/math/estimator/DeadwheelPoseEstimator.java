package xbot.common.math.estimator;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.estimator.DifferentialDrivePoseEstimator;
import edu.wpi.first.math.estimator.PoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import xbot.common.math.kinematics.DeadwheelKinematics;
import xbot.common.math.kinematics.DeadwheelOdometry;
import xbot.common.math.kinematics.DeadwheelWheelPositions;

/**
 * This class wraps {@link DifferentialDriveOdometry Differential Drive
 * Odometry} to fuse
 * latency-compensated vision measurements with differential drive encoder
 * measurements. It is
 * intended to be a drop-in replacement for {@link DifferentialDriveOdometry};
 * in fact, if you never
 * call {@link DifferentialDrivePoseEstimator#addVisionMeasurement} and only
 * call {@link
 * DifferentialDrivePoseEstimator#update} then this will behave exactly the same
 * as
 * DifferentialDriveOdometry.
 *
 * <p>
 * {@link DifferentialDrivePoseEstimator#update} should be called every robot
 * loop.
 *
 * <p>
 * {@link DifferentialDrivePoseEstimator#addVisionMeasurement} can be called as
 * infrequently as
 * you want; if you never call it then this class will behave exactly like
 * regular encoder odometry.
 */
public class DeadwheelPoseEstimator extends PoseEstimator<DeadwheelWheelPositions> {
    /**
     * Constructs a DifferentialDrivePoseEstimator with default standard deviations
     * for the model and
     * vision measurements.
     *
     * <p>
     * The default standard deviations of the model states are 0.02 meters for x,
     * 0.02 meters for
     * y, and 0.01 radians for heading. The default standard deviations of the
     * vision measurements are
     * 0.1 meters for x, 0.1 meters for y, and 0.1 radians for heading.
     *
     * @param kinematics          A correctly-configured kinematics object for your
     *                            drivetrain.
     * @param gyroAngle           The current gyro angle.
     * @param leftDistanceMeters  The distance traveled by the left encoder.
     * @param rightDistanceMeters The distance traveled by the right encoder.
     * @param initialPoseMeters   The starting pose estimate.
     */
    public DeadwheelPoseEstimator(
            DeadwheelKinematics kinematics,
            Rotation2d gyroAngle,
            double leftDistanceMeters,
            double rightDistanceMeters,
            double frontDistanceMeters,
            double rearDistanceMeters,
            Pose2d initialPoseMeters) {
        this(
                kinematics,
                gyroAngle,
                leftDistanceMeters,
                rightDistanceMeters,
                frontDistanceMeters,
                rearDistanceMeters,
                initialPoseMeters,
                VecBuilder.fill(0.02, 0.02, 0.01),
                VecBuilder.fill(0.1, 0.1, 0.1));
    }

    /**
     * Constructs a DifferentialDrivePoseEstimator.
     *
     * @param kinematics               A correctly-configured kinematics object for
     *                                 your drivetrain.
     * @param gyroAngle                The gyro angle of the robot.
     * @param leftDistanceMeters       The distance traveled by the left encoder.
     * @param rightDistanceMeters      The distance traveled by the right encoder.
     * @param initialPoseMeters        The estimated initial pose.
     * @param stateStdDevs             Standard deviations of the pose estimate (x
     *                                 position in meters, y position
     *                                 in meters, and heading in radians). Increase
     *                                 these numbers to trust your state estimate
     *                                 less.
     * @param visionMeasurementStdDevs Standard deviations of the vision pose
     *                                 measurement (x position
     *                                 in meters, y position in meters, and heading
     *                                 in radians). Increase these numbers to trust
     *                                 the vision pose measurement less.
     */
    public DeadwheelPoseEstimator(
            DeadwheelKinematics kinematics,
            Rotation2d gyroAngle,
            double leftDistanceMeters,
            double rightDistanceMeters,
            double frontDistanceMeters,
            double rearDistanceMeters,
            Pose2d initialPoseMeters,
            Matrix<N3, N1> stateStdDevs,
            Matrix<N3, N1> visionMeasurementStdDevs) {
        super(
                kinematics,
                new DeadwheelOdometry(
                        gyroAngle, leftDistanceMeters, rightDistanceMeters, frontDistanceMeters, rearDistanceMeters,
                        initialPoseMeters),
                stateStdDevs,
                visionMeasurementStdDevs);
    }

    /**
     * Resets the robot's position on the field.
     *
     * <p>
     * The gyroscope angle does not need to be reset here on the user's robot code.
     * The library
     * automatically takes care of offsetting the gyro angle.
     *
     * @param gyroAngle           The angle reported by the gyroscope.
     * @param leftPositionMeters  The distance traveled by the left encoder.
     * @param rightPositionMeters The distance traveled by the right encoder.
     * @param poseMeters          The position on the field that your robot is at.
     */
    public void resetPosition(
            Rotation2d gyroAngle,
            double leftPositionMeters,
            double rightPositionMeters,
            double frontPositionMeters,
            double rearPositionMeters,
            Pose2d poseMeters) {
        resetPosition(
                gyroAngle,
                new DeadwheelWheelPositions(leftPositionMeters, rightPositionMeters, frontPositionMeters,
                        rearPositionMeters),
                poseMeters);
    }

    /**
     * Updates the pose estimator with wheel encoder and gyro information. This
     * should be called every
     * loop.
     *
     * @param gyroAngle           The current gyro angle.
     * @param distanceLeftMeters  The total distance travelled by the left wheel in
     *                            meters.
     * @param distanceRightMeters The total distance travelled by the right wheel in
     *                            meters.
     * @return The estimated pose of the robot in meters.
     */
    public Pose2d update(
            Rotation2d gyroAngle, double distanceLeftMeters, double distanceRightMeters, double distanceFrontMeters,
            double distanceRearMeters) {
        return update(
                gyroAngle, new DeadwheelWheelPositions(distanceLeftMeters, distanceRightMeters, distanceFrontMeters,
                        distanceRearMeters));
    }

    /**
     * Updates the pose estimator with wheel encoder and gyro information. This
     * should be called every
     * loop.
     *
     * @param currentTimeSeconds  Time at which this method was called, in seconds.
     * @param gyroAngle           The current gyro angle.
     * @param distanceLeftMeters  The total distance travelled by the left wheel in
     *                            meters.
     * @param distanceRightMeters The total distance travelled by the right wheel in
     *                            meters.
     * @return The estimated pose of the robot in meters.
     */
    public Pose2d updateWithTime(
            double currentTimeSeconds,
            Rotation2d gyroAngle,
            double distanceLeftMeters,
            double distanceRightMeters, double distanceFrontMeters, double distanceRearMeters) {
        return updateWithTime(
                currentTimeSeconds,
                gyroAngle,
                new DeadwheelWheelPositions(distanceLeftMeters, distanceRightMeters, distanceFrontMeters,
                        distanceRearMeters));
    }
}
