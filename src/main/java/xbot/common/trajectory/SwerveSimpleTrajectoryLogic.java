package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Twist2d;
import org.apache.logging.log4j.LogManager;
import xbot.common.math.PIDManager;
import xbot.common.math.XYPair;
import xbot.common.subsystems.drive.control_logic.HeadingModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class SwerveSimpleTrajectoryLogic {

    org.apache.logging.log4j.Logger log = LogManager.getLogger(this.getClass());

    private Supplier<List<XbotSwervePoint>> keyPointsProvider;
    private List<XbotSwervePoint> keyPoints;
    private boolean enableConstantVelocity = false;
    private double constantVelocity = 10;
    private boolean stopWhenFinished = true;
    private final SimpleTimeInterpolator interpolator = new SimpleTimeInterpolator();
    SimpleTimeInterpolator.InterpolationResult lastResult;
    double maxPower = 1.0;
    double maxTurningPower = 1.0;

    public SwerveSimpleTrajectoryLogic() {

    }

    // --------------------------------------------------------------
    // Configuration
    // --------------------------------------------------------------

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

    public void setEnableConstantVelocity(boolean enableConstantVelocity) {
        this.enableConstantVelocity = enableConstantVelocity;
    }

    public void setConstantVelocity(double constantVelocity) {
        this.constantVelocity = constantVelocity;
    }

    public List<XbotSwervePoint> getKeyPoints() { return this.keyPointsProvider.get(); }

    public List<XbotSwervePoint> getResolvedKeyPoints() {
        return keyPoints;
    }

    public void setStopWhenFinished(boolean newValue) {
        this.stopWhenFinished = newValue;
    }

    // --------------------------------------------------------------
    // Major Command Elements
    // --------------------------------------------------------------

    public void reset(Pose2d currentPose) {
        log.info("Resetting");
        keyPoints = keyPointsProvider.get();
        log.info("Key points size: " + keyPoints.size());

        var initialPoint = new XbotSwervePoint(currentPose, 0);

        if (enableConstantVelocity) {
            keyPoints = getVelocityAdjustedSwervePoints(initialPoint, keyPoints, constantVelocity);
        }

        interpolator.setMaximumDistanceFromChasePointInInches(24);
        interpolator.setKeyPoints(keyPoints);
        interpolator.initialize(initialPoint);
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
            double velocityAdjustedDuration = distance / velocity;
            velocityAdjustedPoints.add(new XbotSwervePoint(swervePoints.get(i).keyPose, velocityAdjustedDuration));
        }

        return velocityAdjustedPoints;
    }

    public XYPair getGoalVector(Pose2d currentPose) {
        lastResult = interpolator.calculateTarget(currentPose.getTranslation());
        var chasePoint = lastResult.chasePoint;

        XYPair targetPosition = new XYPair(chasePoint.getX(), chasePoint.getY());
        XYPair currentPosition = new XYPair(currentPose.getX(), currentPose.getY());

        // Get the difference between where we are, and where we want to be.
        XYPair goalVector = targetPosition.clone().add(
                currentPosition.scale(-1)
        );

        return goalVector;
    }

    public Twist2d calculatePowers(Pose2d currentPose, PIDManager positionalPid, HeadingModule headingModule) {
        var goalVector = getGoalVector(currentPose);

        // Now that we have a chase point, we can drive to it. The rest of the logic is
        // from our proven SwerveToPointCommand. Eventually, the common components should be
        // refactored and should also move towards WPI objects (e.g. Pose2d rather than FieldPose).

        // PID on the magnitude of the goal. Kind of similar to rotation,
        // our goal is "zero error".
        double magnitudeGoal = goalVector.getMagnitude();
        double drivePower = positionalPid.calculate(magnitudeGoal, 0);

        // Create a vector in the direction of the goal, scaled by the drivePower.
        XYPair intent = XYPair.fromPolar(goalVector.getAngle(), drivePower);

        double headingPower = headingModule.calculateHeadingPower(
                lastResult.chaseHeading.getDegrees());

        if (intent.getMagnitude() > maxPower && maxPower > 0 && intent.getMagnitude() > 0) {
            intent = intent.scale(maxPower / intent.getMagnitude());
        }

        if (maxTurningPower > 0)
        {
            headingPower = headingPower * maxTurningPower;
        }

        return new Twist2d(intent.x, intent.y, headingPower);
    }

    public boolean recommendIsFinished(Pose2d currentPose, PIDManager positionalPid, HeadingModule headingModule) {
        var goalVector = getGoalVector(currentPose);
        // TODO: Move this threshold into a variable
        boolean isAtNoStoppingGoal = goalVector.getMagnitude() < 18; // 18 inches

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


}
