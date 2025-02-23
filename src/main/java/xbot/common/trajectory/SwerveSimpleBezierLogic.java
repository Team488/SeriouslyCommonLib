package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import edu.wpi.first.units.measure.LinearVelocity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.advantage.AKitLogger;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.subsystems.drive.SwerveKinematicsCalculator;
import xbot.common.subsystems.drive.SwervePointKinematics;
import xbot.common.subsystems.drive.SwerveSimpleTrajectoryMode;
import xbot.common.subsystems.drive.control_logic.HeadingModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Seconds;

public class SwerveSimpleBezierLogic {

    Logger log = LogManager.getLogger(this.getClass());
    final AKitLogger aKitLog = new AKitLogger("SimpleInterpolator/");

    private Supplier<List<XbotSwervePoint>> keyPointsProvider;
    private List<XbotSwervePoint> keyPoints;
    private boolean stopWhenFinished = true;
    private final SimpleInterpolator interpolator;
    SimpleInterpolator.InterpolationResult lastResult;
    double maxPower = 1.0;
    double maxTurningPower = 1.0;
    private ProvidesWaypoints waypointRouter;
    private boolean aimAtGoalDuringFinalLeg;
    private boolean aimAtIntermediateNonFinalLegs;
    private boolean driveBackwards = false;
    private boolean enableSpecialAimTarget = false;
    private boolean enableSpecialAimDuringFinalLeg = false;
    private Pose2d specialAimTarget;
    private boolean prioritizeRotationIfCloseToGoal = false;
    private double distanceThresholdToPrioritizeRotation = 1.5;
    private double constantVelocity = 0;
    private SwervePointKinematics globalKinematics = null;
    private SwerveSimpleTrajectoryMode mode = SwerveSimpleTrajectoryMode.DurationInSeconds;
    RobotAssertionManager assertionManager;

    public SwerveSimpleBezierLogic(RobotAssertionManager assertionManager) {
        this.assertionManager = assertionManager;
        interpolator = new SimpleInterpolator(this.assertionManager);
    }

    // --------------------------------------------------------------
    // Configuration
    // --------------------------------------------------------------

    public void setEnableSpecialAimTarget(boolean enableSpecialAimTarget) {
        this.enableSpecialAimTarget = enableSpecialAimTarget;
    }

    public void setEnableSpecialAimDuringFinalLeg(boolean enableSpecialAimDuringFinalLeg) {
        this.enableSpecialAimDuringFinalLeg = enableSpecialAimDuringFinalLeg;
    }

    public void setSpecialAimTarget(Pose2d specialAimTarget) {
        this.specialAimTarget = specialAimTarget;
    }

    public void setAimAtGoalDuringFinalLeg(boolean aimAtGoalDuringFinalLeg) {
        this.aimAtGoalDuringFinalLeg = aimAtGoalDuringFinalLeg;
    }

    public void setAimAtIntermediateNonFinalLegs(boolean aimAtIntermediateNonFinalLegs) {
        this.aimAtIntermediateNonFinalLegs = aimAtIntermediateNonFinalLegs;
    }

    public void setDriveBackwards(boolean driveBackwards) {
        this.driveBackwards = driveBackwards;
    }

    public void setWaypointRouter(ProvidesWaypoints waypointRouter) {
        this.waypointRouter = waypointRouter;
    }

    public void setKeyPoints(List<XbotSwervePoint> keyPoints) {
        setKeyPointsProvider(() -> keyPoints);
    }

    public void setKeyPointsProvider(Supplier<List<XbotSwervePoint>> keyPointsProvider) {
        this.keyPointsProvider = keyPointsProvider;
    }

    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }

    public void setMaxTurningPower(double maxTurningPower) {
        this.maxTurningPower = maxTurningPower;
    }

    public List<XbotSwervePoint> getKeyPoints() {
        return this.keyPointsProvider.get();
    }

    public List<XbotSwervePoint> getResolvedKeyPoints() {
        return keyPoints;
    }

    public void setStopWhenFinished(boolean newValue) {
        this.stopWhenFinished = newValue;
    }

    public void setPrioritizeRotationIfCloseToGoal(boolean prioritizeRotationIfCloseToGoal) {
        this.prioritizeRotationIfCloseToGoal = prioritizeRotationIfCloseToGoal;
    }

    public void setDistanceThresholdToPrioritizeRotation(double distanceThresholdToPrioritizeRotation) {
        this.distanceThresholdToPrioritizeRotation = distanceThresholdToPrioritizeRotation;
    }

    public void setVelocityMode(SwerveSimpleTrajectoryMode mode) {
        this.mode = mode;
    }

    public void setConstantVelocity(double constantVelocity) {
        this.constantVelocity = constantVelocity;
    }

    public void setGlobalKinematicValues(SwervePointKinematics globalKinematics) {
        this.globalKinematics = globalKinematics;
    }

    // --------------------------------------------------------------
    // Major Command Elements
    // --------------------------------------------------------------

    public void reset(Pose2d currentPose) {
        log.info("Resetting");
        keyPoints = keyPointsProvider.get();
        log.debug("Key points size: " + keyPoints.size());

        var initialPoint = new XbotSwervePoint(currentPose, 0);

        // If we have a field, and only 1 target point, use the field to avoid obstacles.
        if (waypointRouter != null && keyPoints.size() == 1) {
            log.info("Generating path avoiding obstacles");

            // Visualize direct raycast
            var start = new XbotSwervePoint(currentPose.getTranslation(), currentPose.getRotation(), 0);
            var raycast = XbotSwervePoint.generateTrajectory(List.of(
                    start, keyPoints.get(0))
            );
            aKitLog.record("Raycast", raycast);

            var targetPoint = keyPoints.get(0);
            keyPoints = waypointRouter.generatePath(currentPose,
                    new Pose2d(targetPoint.getTranslation2d(), targetPoint.getRotation2d()));
        }

        // Adjust points based on mode, since the logic at default is optimized for "DurationInSeconds" we need
        // To convert over modes to have values in DurationInSeconds.
        switch (mode) {
            case DurationInSeconds -> {} // The logic at default is optimized for duration in seconds.
            case KinematicsForIndividualPoints -> {
                for (XbotSwervePoint point : keyPoints) {
                    if (point.getKinematics() == null) {
                        assertionManager.throwException("Needs to set kinematics for swerve point!", new Exception());
                    }
                }
                keyPoints = getKinematicsAdjustedSwervePoints(initialPoint, keyPoints);
            }
            case GlobalKinematicsValue -> {
                if (globalKinematics == null) {
                    assertionManager.throwException("Needs to set globalKinematics!", new Exception());
                }
                keyPoints = getGlobalKinematicsAdjustedSwervePoints(initialPoint, keyPoints, currentPose);
            }
            case ConstantVelocity -> {
                keyPoints = getVelocityAdjustedSwervePoints(initialPoint, keyPoints, constantVelocity);
            }
            default -> assertionManager.throwException("No handling for SwerveSimpleTrajectoryMode!", new Exception());
        }

        handleAimingAtFinalLeg(currentPose);

        if (aimAtIntermediateNonFinalLegs && keyPoints.size() > 1) {
            // When driving through the field, we can avoid obstacles better if we are
            // aligned in our direction of travel - for example, to "just slide past" the stage columns.
            // This doesn't touch the final point; that's handled by the aimAtGoalDuringFinalLeg logic.
            // This means that this only applies to routes that have more than one point.

            // We start by modifying the first point - this is a special case since the "previous point"
            // is our currentPose. After that, we can iterate through the rest of the the points until we reach
            // the second to last point.

            // Modify the first point
            var firstPoint = keyPoints.get(0);
            firstPoint.setPose(new Pose2d(
                    firstPoint.getTranslation2d(),
                    getAngleBetweenTwoPoints(currentPose.getTranslation(), firstPoint.getTranslation2d())));

            // Modify the second through second to last points
            for (int i = 0; i < keyPoints.size() - 1; i++) {
                var currentPoint = keyPoints.get(i);
                var nextPoint = keyPoints.get(i + 1);

                var goalAngle = getAngleBetweenTwoPoints(currentPoint.getTranslation2d(), nextPoint.getTranslation2d());
                nextPoint.setPose(new Pose2d(
                        nextPoint.getTranslation2d(),
                        goalAngle));
            }
        }

        aKitLog.record("Trajectory", XbotSwervePoint.generateTrajectory(keyPoints));

        interpolator.setMinimumDistanceFromChasePointInMeters(0.45);
        interpolator.setKeyPoints(keyPoints);
        interpolator.initialize(initialPoint, mode);
    }

    private void handleAimingAtFinalLeg(Pose2d currentPose) {
        // If aim at goal during final leg, there are two cases:
        // 1. There is only one point, so get the field angle between our current point and the target point.
        // 2. We have a list of points, so get the field angle between the second ot last point and the last point.

        // check for first case
        if (aimAtGoalDuringFinalLeg && keyPoints.size() == 1) {
            var targetPoint = keyPoints.get(0);

            // Adjust the goal of the final point
            targetPoint.setPose(new Pose2d(
                    targetPoint.getTranslation2d(),
                    getAngleBetweenTwoPoints(currentPose.getTranslation(), targetPoint.getTranslation2d())));
        }

        // check for second case
        if (aimAtGoalDuringFinalLeg && keyPoints.size() > 1) {
            var targetPoint = keyPoints.get(keyPoints.size() - 1);
            var secondToLastPoint = keyPoints.get(keyPoints.size() - 2);

            // Adjust the goal of the final point
            targetPoint.setPose(new Pose2d(
                    targetPoint.getTranslation2d(),
                    getAngleBetweenTwoPoints(secondToLastPoint.getTranslation2d(), targetPoint.getTranslation2d())));
        }
    }

    private Rotation2d getAngleBetweenTwoPoints(Translation2d currentTranslation, Translation2d targetTranslation) {
        double deltaY = targetTranslation.getY() - currentTranslation.getY();
        double deltaX = targetTranslation.getX() - currentTranslation.getX();
        var rotation = Rotation2d.fromRadians(Math.atan2(deltaY, deltaX));
        if (driveBackwards) {
            rotation = rotation.plus(Rotation2d.fromDegrees(180));
        }
        return rotation;
    }

    private List<XbotSwervePoint> getVelocityAdjustedSwervePoints(
            XbotSwervePoint initialPoint,
            List<XbotSwervePoint> swervePoints,
            double velocity) {
        // Normally each swerve point has a duration, but it will probably be easier to tune if we control overall velocity instead.
        // To do this, we will need to iterate though each point, dividing the distance between the current point and the next
        // point by the velocity to get a new duration.

        // The first point is a special case, since it's dynamic depending on where the robot actually is to start.
        ArrayList<XbotSwervePoint> velocityAdjustedPoints = new ArrayList<>();

        // Now, the rest follow this general pattern. Compare the current point to the next point, and adjust the duration.
        XbotSwervePoint previous = initialPoint;
        for (int i = 0; i < swervePoints.size(); i++) {
            if (i > 0) {
                // If we've moved on to later points, we can now safely get previous entries in the list.
                previous = swervePoints.get(i - 1);
            }
            var current = swervePoints.get(i);

            double distance = previous.getTranslation2d().getDistance(current.getTranslation2d());
            double velocityAdjustedDuration = distance / velocity;
            if (velocityAdjustedDuration > 0) {
                velocityAdjustedPoints.add(new XbotSwervePoint(swervePoints.get(i).keyPose, velocityAdjustedDuration));
            }
        }

        // If you attempt to invoke this method where the start point and all intermediate points are the same point,
        // then all the points will be obliterated because there's no distance between any of them.
        // However, we should still return something that can represent that we are pretty happy with our position,
        // and has a non-zero duration so the interpolator won't get confused.
        if (velocityAdjustedPoints.size() == 0) {
            var dummyPoint = new XbotSwervePoint(initialPoint.keyPose, 0.05);
            velocityAdjustedPoints.add(dummyPoint);
        }

        return velocityAdjustedPoints;
    }

    private List<XbotSwervePoint> getKinematicsAdjustedSwervePoints(
            XbotSwervePoint initialPoint,
            List<XbotSwervePoint> swervePoints) {

        ArrayList<XbotSwervePoint> adjustedPoints = new ArrayList<>();

        SwerveKinematicsCalculator calculator = null;
        XbotSwervePoint previous = initialPoint;
        for (int i = 0; i < swervePoints.size(); i++) {
            var current = swervePoints.get(i);
            if (i > 0) {
                // If we've moved on to later points, we can now safely get previous entries in the list.
                previous = swervePoints.get(i - 1);
                // Calculate the initial velocity of current node
                current.setKinematics(current.kinematics.kinematicsWithNewInitialVelocity(calculator.getVelocityAtFinish()));
            }
            double distance = previous.getTranslation2d().getDistance(current.getTranslation2d());
            calculator = new SwerveKinematicsCalculator(
                    assertionManager,
                    Meters.zero(),
                    Meters.of(distance),
                    current.getKinematics()
            );
            double adjustedDuration = calculator.getTotalOperationTime().in(Seconds);

            if (adjustedDuration > 0) {
                XbotSwervePoint point = new XbotSwervePoint(current.keyPose, adjustedDuration);
                point.setKinematics(current.getKinematics());
                adjustedPoints.add(point);
            }
        }

        if (adjustedPoints.size() == 0) {
            var dummyPoint = new XbotSwervePoint(initialPoint.keyPose, 0.05);
            adjustedPoints.add(dummyPoint);
        }

        return adjustedPoints;
    }

    // Instead of using kinematic values of each individual SwervePoint
    // We use kinematic values "globalKinematics" set in this logic
    private List<XbotSwervePoint> getGlobalKinematicsAdjustedSwervePoints(
            XbotSwervePoint initialPoint,
            List<XbotSwervePoint> swervePoints,
            Pose2d startingPose) {

        ArrayList<XbotSwervePoint> adjustedPoints = new ArrayList<>();

        double totalDistance = 0;
        Translation2d currentPosition = startingPose.getTranslation();
        for (XbotSwervePoint point : swervePoints) {
            totalDistance += currentPosition.getDistance(point.keyPose.getTranslation());
            currentPosition = point.keyPose.getTranslation();
        }

        SwerveKinematicsCalculator calculator = new SwerveKinematicsCalculator(
                assertionManager,
                Meters.zero(),
                Meters.of(totalDistance),
                globalKinematics
        );

        double accumulatedDistance = 0;
        XbotSwervePoint previous = initialPoint;
        for (int i = 0; i < swervePoints.size(); i++) {
            var current = swervePoints.get(i);
            if (i > 0) {
                // If we've moved on to later points, we can now safely get previous entries in the list.
                previous = swervePoints.get(i - 1);
            }

            // NEED: acceleration, initialVelocity, finalVelocity, maxVelocity,
            // we got a and vMax which is global now we need vInitial and vFinal
            LinearVelocity vi = calculator.getVelocityAtDistanceTravelled(Meters.of(accumulatedDistance));
            double distance = previous.getTranslation2d().getDistance(current.getTranslation2d());
            accumulatedDistance += distance;
            LinearVelocity vf = calculator.getVelocityAtDistanceTravelled(Meters.of(accumulatedDistance));

            SwerveKinematicsCalculator operationCalculator = new SwerveKinematicsCalculator(
                    assertionManager,
                    Meters.zero(),
                    Meters.of(distance),
                    new SwervePointKinematics(globalKinematics.acceleration(), vi, vf, globalKinematics.maxVelocity())
            );

            double adjustedDuration = operationCalculator.getTotalOperationTime().in(Seconds);

            if (adjustedDuration > 0) {
                XbotSwervePoint point = new XbotSwervePoint(current.keyPose, adjustedDuration);
                point.setKinematics(new SwervePointKinematics(globalKinematics.acceleration(), vi, vf, globalKinematics.maxVelocity()));
                adjustedPoints.add(point);
            }
        }

        if (adjustedPoints.size() == 0) {
            var dummyPoint = new XbotSwervePoint(initialPoint.keyPose, 0.05);
            adjustedPoints.add(dummyPoint);
        }

        return adjustedPoints;
    }

    public XYPair getGoalVector(Pose2d currentPose) {
        lastResult = interpolator.calculateTarget(currentPose.getTranslation());
        var chasePoint = lastResult.chasePoint;

        aKitLog.record("chasePoint", new Pose2d(chasePoint, lastResult.chaseHeading));

        XYPair targetPosition = new XYPair(chasePoint.getX(), chasePoint.getY());
        XYPair currentPosition = new XYPair(currentPose.getX(), currentPose.getY());

        // Get the difference between where we are, and where we want to be.
        XYPair goalVector = targetPosition.clone().add(
                currentPosition.scale(-1)
        );

        return goalVector;
    }

    public Twist2d calculatePowers(Pose2d currentPose, PIDManager positionalPid, HeadingModule headingModule) {
        return calculatePowers(currentPose, positionalPid, headingModule, 0);
    }

    public Twist2d calculatePowers(Pose2d currentPose, PIDManager positionalPid, HeadingModule headingModule, double maximumVelocity) {
        var goalVector = getGoalVector(currentPose);

        // Now that we have a chase point, we can drive to it. The rest of the logic is
        // from our proven SwerveToPointCommand. Eventually, the common components should be
        // refactored and should also move towards WPI objects (e.g. Pose2d rather than FieldPose).

        // PID on the magnitude of the goal (chase point). Kind of similar to rotation,
        // our goal is "zero error".
        aKitLog.record("goalVector", goalVector);
        double magnitudeGoal = goalVector.getMagnitude();
        aKitLog.record("magnitudeGoal", magnitudeGoal);
        double drivePower = positionalPid.calculate(magnitudeGoal, 0);

        // Create a vector in the direction of the goal, scaled by the drivePower.
        XYPair intent = XYPair.fromPolar(goalVector.getAngle(), drivePower);
        aKitLog.record("intent", intent);

        double degreeTarget = lastResult.chaseHeading.getDegrees();

        boolean lastLegAndSpecialAim = enableSpecialAimDuringFinalLeg && lastResult.isOnFinalLeg;

        if (specialAimTarget != null && (enableSpecialAimTarget || lastLegAndSpecialAim)) {
            degreeTarget = getAngleBetweenTwoPoints(
                    currentPose.getTranslation(), specialAimTarget.getTranslation()
            ).getDegrees();
        }

        double headingPower = headingModule.calculateHeadingPower(
                degreeTarget);

        if (intent.getMagnitude() > maxPower && maxPower > 0 && intent.getMagnitude() > 0) {
            intent = intent.scale(maxPower / intent.getMagnitude());
        }

        if (maxTurningPower > 0) {
            headingPower = headingPower * maxTurningPower;
        }

        aKitLog.record("UpdatedIntent", intent);

        // If we have no max velocity set, or we are on the last point and almost done, just use the position PID
        if (maximumVelocity <= 0 || (lastResult.isOnFinalPoint && lastResult.distanceToTargetPoint < 0.5) || lastResult.lerpFraction > 1) {
            return new Twist2d(intent.x, intent.y, headingPower);

        } else {
            // Otherwise, add the known velocity vector, so we can catch up to our goal.
            var plannedVelocityVector = lastResult.plannedVector;

            // This does some fancy dot product magic so that we go slower if we are off-track
            // Giving PositionalPID additional time to push us back to our desired route
            if (goalVector.getMagnitude() > 0.33) {
                var normalizedGoalVector = goalVector.scale(1 / goalVector.getMagnitude());
                var normalizedVelocityVector = plannedVelocityVector.times(1 / plannedVelocityVector.getNorm());
                double dotProduct = normalizedGoalVector.x * normalizedVelocityVector.getX()
                        + normalizedGoalVector.y * normalizedVelocityVector.getY();
                double clampedDotProduct = Math.max(0, dotProduct);
                aKitLog.record("clampedDotProduct", clampedDotProduct);
                plannedVelocityVector = plannedVelocityVector.times(clampedDotProduct);
            }

            // Scale the planned vector, which is currently in velocity space, to "power space" so we can add it to the intent.
            // This makes our scaledPlannedVector to be a ratio with maximumVelocity, to represent amount of power to input.
            var scaledPlannedVector = plannedVelocityVector.div(maximumVelocity);
            aKitLog.record("scaledPlannedVector", scaledPlannedVector);

            // Add our natural velocity vector to the PositionPID powers into a combined vector
            // NOTE: This combinedVector is in "power space" so it SHOULD be in the range of [-1, 1]
            var combinedVector = scaledPlannedVector.plus(new Translation2d(intent.x, intent.y));

            // If we have *somehow* gone above 100% power, scale it back to magnitude of 1
            if (combinedVector.getNorm() > 1) {
                combinedVector = combinedVector.div(combinedVector.getNorm());
            }

            // If we're close to our target but not aimed at it, scale back translation so rotation can finish
            boolean featureEnabledAndRotationCommanded = prioritizeRotationIfCloseToGoal && Math.abs(headingPower) > 0.075;
            boolean closeAndOnFinalLeg = lastResult.isOnFinalLeg && lastResult.distanceToTargetPoint < distanceThresholdToPrioritizeRotation;
            boolean engageTranslationSlowdown = featureEnabledAndRotationCommanded && closeAndOnFinalLeg;
            if (engageTranslationSlowdown) {
                combinedVector = combinedVector.times(0.01);
            }

            aKitLog.record("FeatureEnabledAndRotationCommanded", featureEnabledAndRotationCommanded);
            aKitLog.record("CloseAndOnFinalLeg", closeAndOnFinalLeg);
            aKitLog.record("TranslationReducedDueToRotation", engageTranslationSlowdown);
            aKitLog.record("HeadingPower", headingPower);
            aKitLog.record("combinedVector", combinedVector);

            return new Twist2d(combinedVector.getX(), combinedVector.getY(), headingPower);
        }
    }

    public boolean recommendIsFinished(Pose2d currentPose, PIDManager positionalPid, HeadingModule headingModule) {
        var goalVector = getGoalVector(currentPose);
        // TODO: Move this threshold into a variable
        boolean isAtNoStoppingGoal = goalVector.getMagnitude() < 0.40;

        boolean finished = (stopWhenFinished ? positionalPid.isOnTarget() : isAtNoStoppingGoal) && headingModule.isOnTarget()
                && lastResult.isOnFinalPoint;
        if (finished) {
            log.info(String.format("SwerveLogic recommends Finished, goal is %f away.", goalVector.getMagnitude()));
        }
        return finished;
    }

    public SimpleInterpolator.InterpolationResult getLastResult() {
        return lastResult;
    }
}
