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

public class SimpleInterpolator {


    ProvidesInterpolationData baseline;
    int index;
    double minimumDistanceFromChasePointInMeters = 0.3;

    SwerveKinematicsCalculator calculator;

    private List<? extends ProvidesInterpolationData> keyPoints;

    Logger log = LogManager.getLogger(SimpleInterpolator.class);
    AKitLogger aKitLog = new AKitLogger("SimpleInterpolator/");
    RobotAssertionManager assertionManager;
    private boolean usingKinematics;

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

    public SimpleInterpolator(RobotAssertionManager assertionManager) {
        this.assertionManager = assertionManager;
    }

    public void setKeyPoints(List<? extends ProvidesInterpolationData > keyPoints) {
        this.keyPoints = keyPoints;
    }

    public void setMinimumDistanceFromChasePointInMeters(double minimumDistanceFromChasePointInMeters) {
        this.minimumDistanceFromChasePointInMeters = minimumDistanceFromChasePointInMeters;
    }

    public void initialize(ProvidesInterpolationData baseline, SwerveSimpleTrajectoryMode mode) {
        log.debug("Initializing a SimpleTimeInterpolator");
        this.baseline = baseline;
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
        // If we have no key points to visit, do nothing.
        if (keyPoints == null || keyPoints.size() == 0) {
            log.warn("No key points to visit!");
            return new InterpolationResult(currentLocation, true, Rotation2d.fromDegrees(0));
        }

        // Get the current target key point.
        var targetKeyPoint = keyPoints.get(index);

        if (targetKeyPoint.getSecondsForSegment() <= 0) {
            log.warn("Cannot have a keypoint with a time of 0 or less!");
            return new InterpolationResult(currentLocation, true, targetKeyPoint.getRotation2d());
        }

        if (usingKinematics && calculator == null) {
            calculator = newCalculator(targetKeyPoint.getTranslation2d(), targetKeyPoint.getKinematics());
        }

        // Set the chase point to be exactly the target key point.
        Translation2d chasePoint = targetKeyPoint.getTranslation2d();

        // Instead of smoothly interpolating, wait at the target key point until the robot gets close.
        if (currentLocation.getDistance(chasePoint) < minimumDistanceFromChasePointInMeters && index < keyPoints.size() - 1) {
            // Robot is close enough: advance to the next key point.
            baseline = targetKeyPoint;
            index++;
            targetKeyPoint = keyPoints.get(index);
            log.debug("Reached keypoint, advancing to next: " + targetKeyPoint.getTranslation2d());
            if (usingKinematics) {
                calculator = newCalculator(targetKeyPoint.getTranslation2d(), targetKeyPoint.getKinematics());
            }
            chasePoint = targetKeyPoint.getTranslation2d();
        }

        // Calculate plannedVector as the immediate direction from the robot to the chase point.
        Translation2d plannedVector = chasePoint.minus(currentLocation);
        if (usingKinematics) {
            // Optionally, incorporate kinematics into the command.
            Distance totalDistance = Meters.of(baseline.getTranslation2d().minus(targetKeyPoint.getTranslation2d()).getNorm());
            double fractionAlongSegment = 0;
            if (totalDistance.in(Meters) > 0) {
                fractionAlongSegment = baseline.getTranslation2d().getDistance(currentLocation) / totalDistance.in(Meters);
            }
            Distance expectedMagnitudeTravelled = calculator.getDistanceTravelledAtCompletionPercentage(fractionAlongSegment);
            LinearVelocity velocityScalar = calculator.getVelocityAtDistanceTravelled(expectedMagnitudeTravelled);
            if (plannedVector.getNorm() > 0) {
                plannedVector = plannedVector.times(velocityScalar.in(MetersPerSecond) / plannedVector.getNorm());
            }
        }

        boolean targetingFinalPoint = (index == keyPoints.size() - 1)
                && (currentLocation.getDistance(chasePoint) < minimumDistanceFromChasePointInMeters);
        boolean isOnFinalLeg = (index == keyPoints.size() - 1);
        double distanceToTarget = currentLocation.getDistance(targetKeyPoint.getTranslation2d());
        // Since we are not interpolating along a time fraction, set lerpFraction to 0.
        double lerpFraction = 0;
        return new InterpolationResult(chasePoint, targetingFinalPoint, targetKeyPoint.getRotation2d(), plannedVector,
                distanceToTarget, lerpFraction, isOnFinalLeg);
    }
}