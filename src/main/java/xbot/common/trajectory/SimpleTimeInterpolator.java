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
import xbot.common.subsystems.drive.DeCasteljau;
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
    private boolean usingBezier;

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

    public void initialize(ProvidesInterpolationData baseline, SwerveSimpleTrajectoryMode mode) {
        log.debug("Initializing a SimpleTimeInterpolator");
        this.baseline = baseline;
        accumulatedProductiveSeconds = 0;
        previousTimestamp = XTimer.getFPGATimestamp();
        index = 0;
        calculator = null;

        usingKinematics = (mode == SwerveSimpleTrajectoryMode.KinematicsForIndividualPoints) || (mode == SwerveSimpleTrajectoryMode.GlobalKinematicsValue);
        usingBezier = (mode == SwerveSimpleTrajectoryMode.BezierCurves);
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

        if (usingKinematics && calculator == null) {
            calculator = newCalculator(
                    targetKeyPoint.getTranslation2d(),
                    targetKeyPoint.getKinematics()
            );
        }

        // First, assume we are just going to our target. (This is what all trajectories will eventually
        // settle to - all this interpolation is for intermediate points.)
        Translation2d chasePoint = targetKeyPoint.getTranslation2d();

        // Now, try to find a better point via linear interpolation.
        // LerpFraction ranges from 0-1, and it is our goal completion progress to chasePoint
        double lerpFraction = (accumulatedProductiveSeconds) / targetKeyPoint.getSecondsForSegment();
        aKitLog.record("LerpFraction", lerpFraction);
        aKitLog.record("accumulatedProductiveSeconds", accumulatedProductiveSeconds);


        // If the fraction is above 1, it's time to set a new baseline point and start LERPing on the next point
        if (lerpFraction >= 1 && index < keyPoints.size()-1) {
            // What was our target now becomes our baseline.
            baseline = targetKeyPoint;
            accumulatedProductiveSeconds = 0;
            lerpFraction = 0;
            log.debug("LerpFraction is above one, so advancing to next keypoint");
            index++;

            // And set our new target to the next element of the list
            targetKeyPoint = keyPoints.get(index);
            log.debug("Baseline is now " + baseline.getTranslation2d()
                    + " and target is now " + targetKeyPoint.getTranslation2d());

            if (usingKinematics) {
                calculator = newCalculator(targetKeyPoint.getTranslation2d(), targetKeyPoint.getKinematics());
            }
        }

        // Most of the time, the fraction will be less than one.
        // In that case, we want to interpolate between the baseline and the target.
        if (lerpFraction < 1) {
            if (usingKinematics) {
                // This will be a curve as the calculator will do some fancy stuff with acceleration and velocity
                Distance expectedMagnitudeTravelled = calculator.getDistanceTravelledAtCompletionPercentage(lerpFraction);
                double multiplier = expectedMagnitudeTravelled.div(calculator.getTotalOperationDistance()).in(Value);
                chasePoint = baseline.getTranslation2d().interpolate(
                        targetKeyPoint.getTranslation2d(), multiplier);
            } else if (usingBezier) {
                chasePoint = DeCasteljau.deCasteljau(
                        baseline.getTranslation2d(),
                        targetKeyPoint.getTranslation2d(),
                        targetKeyPoint.getBezierCurveInfo().controlPoints(),
                        lerpFraction
                );
            } else {
                    // This will be a linear interpolation
                    chasePoint = baseline.getTranslation2d().interpolate(
                            targetKeyPoint.getTranslation2d(), lerpFraction);
            }
        }

        // Now, if that chase point is "too far ahead", we need to freeze the chasePoint to allow the robot to catch up!
        // Ideally, this should NEVER happen unless there are outside interference, and we will rely on PID to push us near
        // the chase point again and everything will continue like normal. This effectively "rewinds time" for the next loop.
        if (currentLocation.getDistance(chasePoint) > maximumDistanceFromChasePointInMeters) {
            accumulatedProductiveSeconds -= secondsSinceLastExecute;
        }

        // The plannedVector is our speed at the current time, IRL and in simulation, this may be behind
        // due to friction, but that's what PID is for.
        var plannedVector = targetKeyPoint.getTranslation2d().minus(baseline.getTranslation2d());

        if (usingKinematics) {
            Distance expectedMagnitudeTravelled = calculator.getDistanceTravelledAtCompletionPercentage(lerpFraction);
            LinearVelocity velocityScalar = calculator.getVelocityAtDistanceTravelled(expectedMagnitudeTravelled);

            // We have a velocity, so we now only need to scale our plannedVector to that
            plannedVector = plannedVector.times(velocityScalar.in(MetersPerSecond) / plannedVector.getNorm());
        } else if (usingBezier) {
            plannedVector = plannedVector.times(targetKeyPoint.getBezierCurveInfo().speed().in(MetersPerSecond) / plannedVector.getNorm());
        } else {
            plannedVector = plannedVector.div(targetKeyPoint.getSecondsForSegment());
        }

        boolean targetingFinalPoint = index == keyPoints.size()-1 && lerpFraction >= 1;
        boolean isOnFinalLeg = index == keyPoints.size()-1;
        return new InterpolationResult(
                chasePoint,
                targetingFinalPoint,
                usingBezier ? new Rotation2d(Math.atan2(chasePoint.getY() - currentLocation.getY(), chasePoint.getX()  - currentLocation.getX())):
                        targetKeyPoint.getRotation2d(),
                plannedVector,
                currentLocation.getDistance(targetKeyPoint.getTranslation2d()), lerpFraction, isOnFinalLeg);
    }
}