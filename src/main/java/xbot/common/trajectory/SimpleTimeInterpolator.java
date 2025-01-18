package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xbot.common.advantage.AKitLogger;
import xbot.common.controls.sensors.XTimer;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.subsystems.drive.SwerveKinematicsCalculator;
import xbot.common.subsystems.drive.SwervePointKinematics;
import xbot.common.subsystems.drive.SwerveSimpleTrajectoryMode;

import java.util.List;
public class SimpleTimeInterpolator {

    double accumulatedProductiveSeconds;
    double previousTimestamp;
    ProvidesInterpolationData baseline;
    int index;
    double maximumDistanceFromChasePointInMeters = 0.3;

    SwerveKinematicsCalculator calculator;

    private List<? extends ProvidesInterpolationData> keyPoints;

    Logger log = LogManager.getLogger(SimpleTimeInterpolator.class);
    AKitLogger aKitLog = new AKitLogger("SimpleTimeInterpolator/");
    RobotAssertionManager assertionManager;

    public class InterpolationResult {
        public Translation2d chasePoint;
        public boolean isOnFinalPoint;

        public Rotation2d chaseHeading;

        public Translation2d plannedVector;

        public double distanceToTargetPoint;

        public double lerpFraction;
        public boolean isOnFinalLeg;

        public InterpolationResult(Translation2d chasePoint, boolean isOnFinalPoint) {
            this(chasePoint, isOnFinalPoint, null);
        }

        public InterpolationResult(Translation2d chasePoint, boolean isOnFinalPoint, Rotation2d chaseHeading) {
            this(chasePoint, isOnFinalPoint, chaseHeading, null);
        }

        public InterpolationResult(Translation2d chasePoint, boolean isOnFinalPoint, Rotation2d chaseHeading,
                                   Translation2d plannedVector) {
            this(chasePoint, isOnFinalPoint, chaseHeading, plannedVector, 0);
        }

        public InterpolationResult(Translation2d chasePoint, boolean isOnFinalPoint, Rotation2d chaseHeading,
                                   Translation2d plannedVector, double distanceToTargetPoint) {
            this(chasePoint, isOnFinalPoint, chaseHeading, plannedVector, distanceToTargetPoint, 0);
        }

        public InterpolationResult(Translation2d chasePoint, boolean isOnFinalPoint, Rotation2d chaseHeading,
                                   Translation2d plannedVector, double distanceToTargetPoint, double lerpFraction) {
            this(chasePoint, isOnFinalPoint, chaseHeading, plannedVector, distanceToTargetPoint, lerpFraction, false);
        }

        public InterpolationResult(Translation2d chasePoint, boolean isOnFinalPoint, Rotation2d chaseHeading,
                                   Translation2d plannedVector, double distanceToTargetPoint, double lerpFraction,
                                   boolean isOnFinalLeg) {
            this.chasePoint = chasePoint;
            this.isOnFinalPoint = isOnFinalPoint;
            this.chaseHeading = chaseHeading;
            this.plannedVector = plannedVector;
            this.distanceToTargetPoint = distanceToTargetPoint;
            this.lerpFraction = lerpFraction;
            this.isOnFinalLeg = isOnFinalLeg;
        }
    }

    public SimpleTimeInterpolator(RobotAssertionManager assertionManager) {
        this.assertionManager = assertionManager;
    }

    public void setKeyPoints(List<? extends ProvidesInterpolationData > keyPoints) {
        this.keyPoints = keyPoints;
    }

    public void setMaximumDistanceFromChasePointInMeters(double maximumDistanceFromChasePointInMeters) {
        this.maximumDistanceFromChasePointInMeters = maximumDistanceFromChasePointInMeters;
    }

    public void initialize(ProvidesInterpolationData baseline) {
        log.info("Initializing a SimpleTimeInterpolator");
        this.baseline = baseline;
        accumulatedProductiveSeconds = 0;
        previousTimestamp = XTimer.getFPGATimestamp();
        index = 0;
        aKitLog.record("freeze proc", false);
    }

    public SwerveKinematicsCalculator newCalculator(Translation2d targetPointTranslation2d, SwervePointKinematics kinematics) {
        return new SwerveKinematicsCalculator(
                assertionManager,
                0,
                baseline.getTranslation2d().minus(targetPointTranslation2d).getNorm(),
                kinematics
        );
    }

    public void resetCalculator() {
        calculator = null;
    }

    public InterpolationResult calculateTarget(Translation2d currentLocation, SwerveSimpleTrajectoryMode mode) {
        double currentTime = XTimer.getFPGATimestamp();
        aKitLog.record("CurrentTime", currentTime);

        double secondsSinceLastExecute = currentTime - previousTimestamp;
        previousTimestamp = currentTime;

        accumulatedProductiveSeconds += secondsSinceLastExecute;

        // If we somehow have no points to visit, don't do anything.

        if (keyPoints == null || keyPoints.size() == 0) {
            log.warn("No key points to visit!");
            return new InterpolationResult(currentLocation, true, Rotation2d.fromDegrees(0));
        }

        var targetKeyPoint = keyPoints.get(index);

        if (targetKeyPoint.getSecondsForSegment() <= 0) {
            log.warn("Cannot have a keypoint with a time of 0 or less!");
            return new InterpolationResult(currentLocation, true, targetKeyPoint.getRotation2d());
        }

        if ((mode == SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints
                || mode == SwerveSimpleTrajectoryMode.KinematicsForPointsList) && calculator == null) {
            calculator = newCalculator(
                    targetKeyPoint.getTranslation2d(),
                    targetKeyPoint.getKinematics()
            );
        }

        // First, assume we are just going to our target. (This is what all trajectories will eventually
        // settle to - all this interpolation is for intermediate points.)
        Translation2d chasePoint = targetKeyPoint.getTranslation2d();

        // Now, try to find a better point via linear interpolation.
        double lerpFraction = (accumulatedProductiveSeconds) / targetKeyPoint.getSecondsForSegment();
        aKitLog.record("LerpFraction", lerpFraction);
        aKitLog.record("accumulatedProductiveSeconds", accumulatedProductiveSeconds);


        // If the fraction is above 1, it's time to set a new baseline point and start LERPing on the next
        // one.
        if (lerpFraction >= 1 && index < keyPoints.size()-1) {
            // What was our target now becomes our baseline.
            baseline = targetKeyPoint;
            accumulatedProductiveSeconds = 0;
            lerpFraction = 0;
            log.info("LerpFraction is above one, so advancing to next keypoint");
            index++;
            // And set our new target to the next element of the list
            targetKeyPoint = keyPoints.get(index);
            log.info("Baseline is now " + baseline.getTranslation2d()
                    + " and target is now " + targetKeyPoint.getTranslation2d());

            if (mode == SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints || mode == SwerveSimpleTrajectoryMode.KinematicsForPointsList) {
                calculator = newCalculator(
                        targetKeyPoint.getTranslation2d(),
                        targetKeyPoint.getKinematics()
                );
            }
        }

        // Most of the time, the fraction will be less than one.
        // In that case, we want to interpolate between the baseline and the target.
        double multiplier = 1;
        if (lerpFraction < 1) {
            if (mode == SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints || mode == SwerveSimpleTrajectoryMode.KinematicsForPointsList) {
                double magnitude = calculator.geDistanceTravelledAtCompletionPercentage(lerpFraction);
                multiplier = magnitude / calculator.getTotalDistanceInMeters(); // Magnitude progress
                chasePoint = baseline.getTranslation2d().interpolate(
                        targetKeyPoint.getTranslation2d(), multiplier);
            } else {
                chasePoint = baseline.getTranslation2d().interpolate(
                        targetKeyPoint.getTranslation2d(), lerpFraction);
            }
        }

        // But if that chase point is "too far ahead", we need to freeze the chasePoint
        if (currentLocation.getDistance(chasePoint) > maximumDistanceFromChasePointInMeters) {
            // This effectively "rewinds time" for the next loop.
            aKitLog.record("Freezing ChasePoint", true);
            accumulatedProductiveSeconds -= secondsSinceLastExecute;
        }

        // The planned velocity is the same (for now) at all points between the baseline and the target.
        var plannedVector = targetKeyPoint.getTranslation2d().minus(baseline.getTranslation2d())
                .div(targetKeyPoint.getSecondsForSegment());

        plannedVector.times(multiplier > 0 ? multiplier : 0.01);

        boolean targetingFinalPoint = index == keyPoints.size()-1 && lerpFraction >= 1;
        boolean isOnFinalLeg = index == keyPoints.size()-1;
        return new InterpolationResult(chasePoint, targetingFinalPoint, targetKeyPoint.getRotation2d(), plannedVector,
                currentLocation.getDistance(targetKeyPoint.getTranslation2d()), lerpFraction, isOnFinalLeg);
    }
}