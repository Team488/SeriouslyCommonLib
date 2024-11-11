package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import xbot.common.math.WrappedRotation2d;
import xbot.common.subsystems.pose.BasePoseSubsystem;

import java.util.ArrayList;
import java.util.List;

public class XbotSwervePoint implements ProvidesInterpolationData {

    public Pose2d keyPose;

    public double secondsToPoint;
    double aMax = 0;
    double vi = 0;
    double vf = 0;
    double vMax = 0;

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

    public void setKinematicValues(double aMax, double vi, double vf, double vMax) {
        this.aMax = aMax;
        this.vi = vi;
        this.vf = Math.max(Math.min(vMax, vf), -vMax);
        this.vMax = vMax;
    }

    public void setPose(Pose2d pose) {
        this.keyPose = pose;
    }

    public static Trajectory generateTrajectory(List<XbotSwervePoint> swervePoints) {
        ArrayList<Trajectory.State> wpiStates = new ArrayList<>();
        for (XbotSwervePoint point : swervePoints) {
            Trajectory.State state = new Trajectory.State();
            // Swerve points are in inches, but the trajectory is in meters.
            state.poseMeters = new Pose2d(
                    point.keyPose.getTranslation().getX(),
                    point.keyPose.getTranslation().getY(),
                    WrappedRotation2d.fromRotation2d(point.keyPose.getRotation())
            );
            state.velocityMetersPerSecond = 0;
            state.accelerationMetersPerSecondSq = 0;
            wpiStates.add(state);
        }
        if (wpiStates.size() == 0) {
            return new Trajectory();
        }
        return new Trajectory(wpiStates);
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

    @Override
    public double getAcceleration() {
        return aMax;
    };

    public double getInitialVelocity() {
        return vi;
    };

    public double getGoalVelocity() {
        return vf;
    };

    public double getMaxVelocity() {
        return vMax;
    };

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