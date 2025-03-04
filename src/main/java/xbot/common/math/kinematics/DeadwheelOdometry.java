package xbot.common.math.kinematics;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.Odometry;
import edu.wpi.first.units.measure.Distance;

/**
 * Class for deadwheel odometry. Odometry allows you to track the robot's
 * position on the
 * field over the course of a match using readings from 2 encoders and a
 * gyroscope.
 *
 * <p>
 * Teams can use odometry during the autonomous period for complex tasks like
 * path following.
 * Furthermore, odometry can be used for latency compensation when using
 * computer-vision systems.
 *
 * <p>
 * It is important that you reset your encoders to zero before using this class.
 * Any subsequent
 * pose resets also require the encoders to be reset to zero.
 */
public class DeadwheelOdometry extends Odometry<DeadwheelWheelPositions> {
    /**
     * Constructs a DeadwheelOdometry object.
     *
     * @param gyroAngle           The angle reported by the gyroscope.
     * @param leftDistanceMeters  The distance traveled by the left encoder.
     * @param rightDistanceMeters The distance traveled by the right encoder.
     * @param initialPoseMeters   The starting position of the robot on the field.
     */
    public DeadwheelOdometry(
            Rotation2d gyroAngle,
            double leftDistanceMeters,
            double rightDistanceMeters,
            double frontDistanceMeters,
            double rearDistanceMeters,
            Pose2d initialPoseMeters) {
        super(
                new DeadwheelKinematics(1),
                gyroAngle,
                new DeadwheelWheelPositions(leftDistanceMeters, rightDistanceMeters, frontDistanceMeters,
                        rearDistanceMeters),
                initialPoseMeters);
    }

    /**
     * Constructs a DeadwheelOdometry object.
     *
     * @param gyroAngle         The angle reported by the gyroscope.
     * @param leftDistance      The distance traveled by the left encoder.
     * @param rightDistance     The distance traveled by the right encoder.
     * @param initialPoseMeters The starting position of the robot on the field.
     */
    public DeadwheelOdometry(
            Rotation2d gyroAngle,
            Distance leftDistance,
            Distance rightDistance,
            Distance frontDistance,
            Distance rearDistance,
            Pose2d initialPoseMeters) {
        this(gyroAngle, leftDistance.in(Meters), rightDistance.in(Meters), frontDistance.in(Meters),
                rearDistance.in(Meters), initialPoseMeters);
    }

    /**
     * Constructs a DeadwheelOdometry object.
     *
     * @param gyroAngle           The angle reported by the gyroscope.
     * @param leftDistanceMeters  The distance traveled by the left encoder.
     * @param rightDistanceMeters The distance traveled by the right encoder.
     */
    public DeadwheelOdometry(
            Rotation2d gyroAngle, double leftDistanceMeters, double rightDistanceMeters, double frontDistanceMeters,
            double rearDistanceMeters) {
        this(gyroAngle, leftDistanceMeters, rightDistanceMeters, frontDistanceMeters, rearDistanceMeters, Pose2d.kZero);
    }

    /**
     * Constructs a DeadwheelOdometry object.
     *
     * @param gyroAngle     The angle reported by the gyroscope.
     * @param leftDistance  The distance traveled by the left encoder.
     * @param rightDistance The distance traveled by the right encoder.
     */
    public DeadwheelOdometry(
            Rotation2d gyroAngle, Distance leftDistance, Distance rightDistance, Distance frontDistance,
            Distance rearDistance) {
        this(gyroAngle, leftDistance, rightDistance, frontDistance, rearDistance, Pose2d.kZero);
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
     * @param leftDistanceMeters  The distance traveled by the left encoder.
     * @param rightDistanceMeters The distance traveled by the right encoder.
     * @param poseMeters          The position on the field that your robot is at.
     */
    public void resetPosition(
            Rotation2d gyroAngle,
            double leftDistanceMeters,
            double rightDistanceMeters,
            double frontDistanceMeters,
            double rearDistanceMeters,
            Pose2d poseMeters) {
        super.resetPosition(
                gyroAngle,
                new DeadwheelWheelPositions(leftDistanceMeters, rightDistanceMeters, frontDistanceMeters,
                        rearDistanceMeters),
                poseMeters);
    }

    /**
     * Resets the robot's position on the field.
     *
     * <p>
     * The gyroscope angle does not need to be reset here on the user's robot code.
     * The library
     * automatically takes care of offsetting the gyro angle.
     *
     * @param gyroAngle     The angle reported by the gyroscope.
     * @param leftDistance  The distance traveled by the left encoder.
     * @param rightDistance The distance traveled by the right encoder.
     * @param poseMeters    The position on the field that your robot is at.
     */
    public void resetPosition(
            Rotation2d gyroAngle, Distance leftDistance, Distance rightDistance, Distance frontDistance,
            Distance rearDistance, Pose2d poseMeters) {
        resetPosition(gyroAngle, leftDistance.in(Meters), rightDistance.in(Meters), frontDistance.in(Meters),
                rearDistance.in(Meters), poseMeters);
    }

    /**
     * Updates the robot position on the field using distance measurements from
     * encoders. This method
     * is more numerically accurate than using velocities to integrate the pose and
     * is also
     * advantageous for teams that are using lower CPR encoders.
     *
     * @param gyroAngle           The angle reported by the gyroscope.
     * @param leftDistanceMeters  The distance traveled by the left encoder.
     * @param rightDistanceMeters The distance traveled by the right encoder.
     * @return The new pose of the robot.
     */
    public Pose2d update(
            Rotation2d gyroAngle, double leftDistanceMeters, double rightDistanceMeters, double frontDistanceMeters,
            double rearDistanceMeters) {
        return super.update(
                gyroAngle, new DeadwheelWheelPositions(leftDistanceMeters, rightDistanceMeters, frontDistanceMeters,
                        rearDistanceMeters));
    }
}
