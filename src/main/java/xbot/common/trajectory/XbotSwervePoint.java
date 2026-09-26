package xbot.common.trajectory;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Translation2d;
import xbot.common.subsystems.drive.SwervePointKinematics;
import xbot.common.subsystems.pose.BasePoseSubsystem;

import java.util.List;

public class XbotSwervePoint implements ProvidesInterpolationData {

    public Pose2d keyPose;

    public double secondsToPoint;
    SwervePointKinematics kinematics;

    public XbotSwervePoint(Pose2d keyPose, double secondsToPoint) {
        this.keyPose = keyPose;
        this.secondsToPoint = secondsToPoint;
    }

    public XbotSwervePoint(Translation2d translation, Rotation2d rotation, double secondsToPoint) {
        this.keyPose = new Pose2d(translation, rotation);
        this.secondsToPoint = secondsToPoint;
    }

    public XbotSwervePoint(double x, double y, double degrees, double secondsToPoint) {
        this.keyPose = new Pose2d(x, y, Rotation2d.fromDegrees(degrees));
        this.secondsToPoint = secondsToPoint;
    }

    public void setKinematics(SwervePointKinematics kinematics) {
        this.kinematics = kinematics;
    }

    public SwervePointKinematics getKinematics() {
        return kinematics;
    }

    public void setPose(Pose2d pose) {
        this.keyPose = pose;
    }

    /**
     * Converts swerve points to the pose array expected by AdvantageKit's trajectory visualization.
     * Timing remains on the source points for use by the simple trajectory interpolators.
     *
     * @param swervePoints points to visualize
     * @return poses in path order
     */
    public static Pose2d[] generatePathVisualization(List<XbotSwervePoint> swervePoints) {
        return swervePoints.stream()
                .map(point -> point.keyPose)
                .toArray(Pose2d[]::new);
    }

    @Override
    public Translation2d getTranslation2d() {
        return keyPose.getTranslation();
    }

    @Override
    public double getSecondsForSegment() {
        return secondsToPoint;
    }

    @Override
    public Rotation2d getRotation2d() {
        return keyPose.getRotation();
    }

    public static XbotSwervePoint createPotentiallyFilppedXbotSwervePoint(
            Translation2d targetLocation, Rotation2d targetHeading, double durationInSeconds) {
        var potentiallyFlippedPose = BasePoseSubsystem.convertBlueToRedIfNeeded(new Pose2d(targetLocation, targetHeading));
        return new XbotSwervePoint(potentiallyFlippedPose, durationInSeconds);
    }

    public static XbotSwervePoint createPotentiallyFilppedXbotSwervePoint(
            Pose2d pose, double durationInSeconds) {
        var potentiallyFlippedPose = BasePoseSubsystem.convertBlueToRedIfNeeded(pose);
        return new XbotSwervePoint(potentiallyFlippedPose, durationInSeconds);
    }

}