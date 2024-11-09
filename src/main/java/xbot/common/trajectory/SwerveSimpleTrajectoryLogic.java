package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Twist2d;
import org.apache.logging.log4j.LogManager;

import xbot.common.advantage.AKitLogger;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.subsystems.drive.SwerveSpeedCalculator;
import xbot.common.subsystems.drive.SwerveSpeedCalculator2;
import xbot.common.subsystems.drive.control_logic.HeadingModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SwerveSimpleTrajectoryLogic {

    org.apache.logging.log4j.Logger log = LogManager.getLogger(this.getClass());
    final AKitLogger aKitLog = new AKitLogger("SimpleTimeInterpolator/");

    private Supplier<List<XbotSwervePoint>> keyPointsProvider;
    private List<XbotSwervePoint> keyPoints;
    private boolean stopWhenFinished = true;
    private final SimpleTimeInterpolator interpolator = new SimpleTimeInterpolator();
    SimpleTimeInterpolator.InterpolationResult lastResult;
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
    private double acceleration = 0.1;
    private double maximumVelocity = 0.5;
    private double velocityAtGoal = 0;

    public SwerveSimpleTrajectoryLogic() {

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

    public List<XbotSwervePoint> getKeyPoints() { return this.keyPointsProvider.get(); }

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

    // --------------------------------------------------------------
    // Major Command Elements
    // --------------------------------------------------------------

    public void reset(Pose2d currentPose) {
        log.info("Resetting");
        keyPoints = keyPointsProvider.get();
        log.info("Key points size: " + keyPoints.size());

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

        keyPoints = adjustSwervePoints(initialPoint, keyPoints);

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

        aKitLog.record("Trajectory",
                XbotSwervePoint.generateTrajectory(keyPoints));

        interpolator.setMaximumDistanceFromChasePointInMeters(0.5);
        interpolator.setKeyPoints(keyPoints);
        interpolator.initialize(initialPoint);
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
        for (int i = 0; i < swervePoints.size(); i++) {

            XbotSwervePoint previous = initialPoint;
            if (i > 0) {
                // If we've moved on to later points, we can now safely get previous entries in the list.
                previous = swervePoints.get(i - 1);
            }
            var current = swervePoints.get(i);

            double distance = previous.getTranslation2d().getDistance(current.getTranslation2d());
            //double velocityAdjustedDuration = distance / velocity;
            double velocityAdjustedDuration = SwerveSpeedCalculator.calculateTime(acceleration,0,0,distance/2) * 2;
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

    private List<XbotSwervePoint> adjustSwervePoints(
            XbotSwervePoint initialPoint,
            List<XbotSwervePoint> swervePoints) {

        ArrayList<XbotSwervePoint> adjustedPoints = new ArrayList<>();

        for (int i = 0; i < swervePoints.size(); i++) {
            XbotSwervePoint previous = initialPoint;
            if (i > 0) {
                // If we've moved on to later points, we can now safely get previous entries in the list.
                previous = swervePoints.get(i - 1);
            }
            var current = swervePoints.get(i);

            double distance = previous.getTranslation2d().getDistance(current.getTranslation2d());
            SwerveSpeedCalculator2 calculator = new SwerveSpeedCalculator2(
                    0,
                    distance,
                    2,
                    0,
                    0,
                    10
            );
            double adjustedDuration = calculator.getTotalOperationTime();

            double velocityAdjustedDuration = SwerveSpeedCalculator.calculateTime(acceleration,0,0,distance/2) * 2;
            if (velocityAdjustedDuration > 0) {
                adjustedPoints.add(new XbotSwervePoint(swervePoints.get(i).keyPose, adjustedDuration));
            }
        }

        if (adjustedPoints.size() == 0) {
            var dummyPoint = new XbotSwervePoint(initialPoint.keyPose, 0.05);
            adjustedPoints.add(dummyPoint);
        }

        return adjustedPoints;
    }

    public XYPair getGoalVector(Pose2d currentPose) {
        lastResult = interpolator.calculateTarget(currentPose.getTranslation(), acceleration);
        var chasePoint = lastResult.chasePoint;

        aKitLog.record("chasePoint", new Pose2d(chasePoint, Rotation2d.fromDegrees(0)));

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

        // PID on the magnitude of the goal. Kind of similar to rotation,
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

        if (specialAimTarget != null && (enableSpecialAimTarget || lastLegAndSpecialAim )) {
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
        }
        else
        {
            // Otherwise, add the known velocity vector, so we can catch up to our goal.

            // Get the dot product between the normalized goal vector and the normalized velocity vector.
            // Quick check for being right on the goal point

            var plannedVelocityVector = lastResult.plannedVector;

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
            double scalarFactor = plannedVelocityVector.getNorm() / maximumVelocity;
            var scaledPlannedVector = plannedVelocityVector.times(scalarFactor / maximumVelocity);
            aKitLog.record("scaledPlannedVector", scaledPlannedVector);
            // Add the PositionPID powers
            var combinedVector = scaledPlannedVector.plus(new Translation2d(intent.x, intent.y));

            // If we've somehow gone above 100% power, scale it back down
            if (combinedVector.getNorm() > 1) {
                combinedVector = combinedVector.times(1 / combinedVector.getNorm());
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

    public SimpleTimeInterpolator.InterpolationResult getLastResult() {
        return lastResult;
    }

    public Twist2d calculatePowers2(
            Pose2d currentPose, PIDManager positionalPid, HeadingModule headingModule, double maximumVelocity) {
        var goalVector = getGoalVector(currentPose);
        double magnitudeGoal = goalVector.getMagnitude();
        double drivePower = positionalPid.calculate(magnitudeGoal, 0);

        // Create a vector in the direction of the goal, scaled by the drivePower.
        XYPair intent = XYPair.fromPolar(goalVector.getAngle(), drivePower);
        return new Twist2d(intent.x, intent.y, 0);
    }
}
