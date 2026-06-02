package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xbot.common.advantage.AKitLogger;
import xbot.common.controls.sensors.XTimer;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.subsystems.drive.SwerveKinematicsCalculator;
import xbot.common.subsystems.drive.SwervePointKinematics;
import xbot.common.subsystems.drive.SwerveSimpleTrajectoryMode;

import java.util.List;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Value;

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
    private boolean usingKinematics;

    public static class InterpolationResult {
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

    public void setKeyPoints(List<? extends ProvidesInterpolationData> keyPoints) {
        this.keyPoints = keyPoints;
    }

    public void setMaximumDistanceFromChasePointInMeters(double maximumDistanceFromChasePointInMeters) {
        this.maximumDistanceFromChasePointInMeters = maximumDistanceFromChasePointInMeters;
    }

    public void initialize(ProvidesInterpolationData baseline, SwerveSimpleTrajectoryMode mode) {
        log.debug("Initializing a SimpleTimeInterpolator");
        this.baseline = baseline;
        accumulatedProductiveSeconds = 0;
        previousTimestamp = XTimer.getFPGATimestamp();
        index = 0;
        calculator = null;
        usingKinematics = (mode == SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints) || (mode == SwerveSimpleTrajectoryMode.GlobalKinematicsValue);
    }

    public SwerveKinematicsCalculator newCalculator(Translation2d targetPointTranslation2d, SwervePointKinematics kinematics) {
        return new SwerveKinematicsCalculator(
                assertionManager,
                Meters.zero(),
                Meters.of(baseline.getTranslation2d().minus(targetPointTranslation2d).getNorm()),
                kinematics
        );
    }

    public InterpolationResult calculateTarget(Translation2d currentLocation) {
        double currentTime = XTimer.getFPGATimestamp();
        aKitLog.record("CurrentTime", currentTime);

        double secondsSinceLastExecute = currentTime - previousTimestamp;
        previousTimestamp = currentTime;

        accumulatedProductiveSeconds += secondsSinceLastExecute;

        if (keyPoints == null || keyPoints.isEmpty()) {
            log.warn("No key points to visit!");
            return new InterpolationResult(currentLocation, true, Rotation2d.fromDegrees(0));
        }

        var targetKeyPoint = keyPoints.get(index);

        if (targetKeyPoint.getSecondsForSegment() <= 0) {
            log.warn("Cannot have a keypoint with a time of 0 or less!");
            return new InterpolationResult(currentLocation, true, targetKeyPoint.getRotation2d());
        }

        if (usingKinematics && calculator == null) {
            calculator = newCalculator(
                    targetKeyPoint.getTranslation2d(),
                    targetKeyPoint.getKinematics()
            );
        }

        Translation2d chasePoint = targetKeyPoint.getTranslation2d();

        aKitLog.record("LerpFraction", accumulatedProductiveSeconds / targetKeyPoint.getSecondsForSegment());
        aKitLog.record("accumulatedProductiveSeconds", accumulatedProductiveSeconds);

        while (index < keyPoints.size() - 1) {
            double segmentTime = targetKeyPoint.getSecondsForSegment();
            if (segmentTime <= 0) {
                log.warn("Cannot have a keypoint with a time of 0 or less! Skipping to next point.");
                index++;
                if (index >= keyPoints.size()) {
                    break;
                }
                targetKeyPoint = keyPoints.get(index);
                if (usingKinematics) {
                    calculator = newCalculator(targetKeyPoint.getTranslation2d(), targetKeyPoint.getKinematics());
                }
                continue;
            }

            if (accumulatedProductiveSeconds < segmentTime) {
                break;
            }

            accumulatedProductiveSeconds -= segmentTime;
            baseline = targetKeyPoint;
            index++;

            targetKeyPoint = keyPoints.get(index);
            log.debug("Advancing to next keypoint, new baseline is {} and new target is {}", baseline.getTranslation2d(), targetKeyPoint.getTranslation2d());
            if (usingKinematics) {
                calculator = newCalculator(targetKeyPoint.getTranslation2d(), targetKeyPoint.getKinematics());
            }
        }

        double lerpFraction = Math.max(0, Math.min(accumulatedProductiveSeconds / targetKeyPoint.getSecondsForSegment(), 1.0));

        if (lerpFraction < 1) {
            if (usingKinematics) {
                Distance expectedMagnitudeTravelled = calculator.getDistanceTravelledAtCompletionPercentage(lerpFraction);
                double multiplier = expectedMagnitudeTravelled.div(calculator.getTotalOperationDistance()).in(Value);
                chasePoint = baseline.getTranslation2d().interpolate(
                        targetKeyPoint.getTranslation2d(), multiplier);
            } else {
                chasePoint = baseline.getTranslation2d().interpolate(
                        targetKeyPoint.getTranslation2d(), lerpFraction);
            }
        }

        // If the chase point is too far ahead, freeze it so the robot can catch up.
        // This "rewinds time" so the next loop recalculates from an earlier point on the trajectory.
        if (currentLocation.getDistance(chasePoint) > maximumDistanceFromChasePointInMeters) {
            accumulatedProductiveSeconds -= secondsSinceLastExecute;
            lerpFraction = Math.max(0, Math.min(accumulatedProductiveSeconds
                    / targetKeyPoint.getSecondsForSegment(), 1.0));
        }

        var plannedVector = targetKeyPoint.getTranslation2d().minus(baseline.getTranslation2d());

        if (usingKinematics) {
            Distance expectedMagnitudeTravelled = calculator.getDistanceTravelledAtCompletionPercentage(lerpFraction);
            LinearVelocity velocityScalar = calculator.getVelocityAtDistanceTravelled(expectedMagnitudeTravelled);

            // We have a velocity, so we now only need to scale our plannedVector to that
            if (plannedVector.getNorm() > 0) {
                plannedVector = plannedVector.times(velocityScalar.in(MetersPerSecond) / plannedVector.getNorm());
            }
        } else {
            plannedVector = plannedVector.div(targetKeyPoint.getSecondsForSegment());
        }

        boolean targetingFinalPoint = index == keyPoints.size()-1 && lerpFraction >= 1;
        boolean isOnFinalLeg = index == keyPoints.size()-1;
        return new InterpolationResult(chasePoint, targetingFinalPoint, targetKeyPoint.getRotation2d(), plannedVector,
                currentLocation.getDistance(targetKeyPoint.getTranslation2d()), lerpFraction, isOnFinalLeg);
    }
}