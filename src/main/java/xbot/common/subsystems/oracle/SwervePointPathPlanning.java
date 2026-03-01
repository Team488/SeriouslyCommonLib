package xbot.common.subsystems.oracle;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.units.measure.Distance;
import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.Radians;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.advantage.AKitLogger;
import xbot.common.injection.electrical_contract.XSwerveDriveElectricalContract;
import xbot.common.trajectory.XbotSwervePoint;
import xbot.common.subsystems.pose.GameField;
import xbot.common.subsystems.pose.IFieldObstacle;
import xbot.common.subsystems.pose.ObstacleMap;

import java.awt.geom.Point2D;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class SwervePointPathPlanning {
    private final Distance radius;
    private final GameField gameField;
    private final ObstacleMap obstacleMap;
    private final AKitLogger aKitLog;

    private static Logger log = LogManager.getLogger(SwervePointPathPlanning.class);

    @Inject
    public SwervePointPathPlanning(ObstacleMap obstacleMap, GameField gameField, XSwerveDriveElectricalContract electrical_contract) {
        this.obstacleMap = obstacleMap;
        this.gameField = gameField;
        this.radius = electrical_contract.getRadiusOfRobot();

        this.aKitLog = new AKitLogger("SwervePointPathPlanning/");
    }

    /**
     * Generates a list of swerve points from Point A -> B while avoiding any obstacles in the map.
     * @param startingPose you are at
     * @param endingPose you want to go to
     * @return a list of swerve points to destination
     */
    public List<XbotSwervePoint> generateSwervePoints(Pose2d startingPose, Pose2d endingPose, boolean allowToughTerrain) {
        List<XbotSwervePoint> swervePoints = new ArrayList<>();
        Translation2d start = startingPose.getTranslation();
        Translation2d end = endingPose.getTranslation();

        // If there's no intersection between the starting point and the collision circle, just proceed
        // directly to the ending point.
        if (!this.obstacleMap.doesRobotPathIntersect(start, end, allowToughTerrain)) {
            swervePoints.add(new XbotSwervePoint(endingPose, 0.001)); // Set to small number so SSTC does not complain.
            return swervePoints;
        }
        var prefix = String.format("/(%.2f,%.2f)->(%.2f,%.2f)", startingPose.getX(), startingPose.getY(), endingPose.getX(), endingPose.getY());
        var closestObstacle = this.obstacleMap.closestObstacle(startingPose, allowToughTerrain).orElseThrow();

        Translation2d tangentPoint = null;
        var trajectoryPoses = new ArrayList<Pose2d>();
        // If we are currently in a rough terrain then it doesn't matter.
        if (this.obstacleMap.doesRobotPathIntersect(start, start, false)) {
            System.out.println("We are in blocked terrain adjusting start.");

            // If we're inside the routing circle and there is a collision, we need to first back out to the routing circle.
            Translation2d direction = start.minus(closestObstacle.center());
            tangentPoint = closestObstacle.center().plus(direction.times(closestObstacle.avoidanceRadius().in(Meters) / direction.getNorm()));
            trajectoryPoses.add(new Pose2d(tangentPoint, startingPose.getRotation()));
            swervePoints.add(new XbotSwervePoint(new Pose2d(tangentPoint, startingPose.getRotation()), 10));
        } else {
            System.out.println("We are not in blocked terrain adjusting start.");

            // otherwise, we're outside the routing circle, and we need to first move to the tangent point.
            tangentPoint = findClosestTangentPoint(start, end, closestObstacle);
            trajectoryPoses.add(new Pose2d(tangentPoint, startingPose.getRotation()));
            swervePoints.add(new XbotSwervePoint(new Pose2d(tangentPoint, startingPose.getRotation()), 0.001));
        }

        aKitLog.record("/closestObstacle/center", closestObstacle.center());
        aKitLog.record("/closestObstacle/avoidanceRadius", closestObstacle.avoidanceRadius());
        aKitLog.record("/firstTangent", new Pose2d(tangentPoint, endingPose.getRotation()));

        int escape = 0;
        while (this.obstacleMap.doesRobotPathIntersect(tangentPoint, end, allowToughTerrain)) {
            escape++;
            tangentPoint = moveAlongCircumference(tangentPoint, end, closestObstacle);
            var tangentPose = new Pose2d(tangentPoint, endingPose.getRotation());
            closestObstacle = this.obstacleMap.closestObstacle(tangentPose, allowToughTerrain).orElseThrow();
            trajectoryPoses.add(tangentPose);
            swervePoints.add(new XbotSwervePoint(tangentPose, 10));
            if (escape > 1000) {
                log.warn("Infinite loop detected in generateSwervePoints, breaking out!");
                break;
            }
        }

        swervePoints.add(new XbotSwervePoint(endingPose, 10));
        log.warn("trajectoryPoses.size: %d%n", trajectoryPoses.size());

        aKitLog.record(prefix + "/trajectory", trajectoryPoses.toArray(new Pose2d[0]));
        return swervePoints;
    }

    private Translation2d findClosestTangentPoint(Translation2d point, Translation2d endPoint, IFieldObstacle closestObstacle) {
        var obstacleCenter = closestObstacle.center();
        var distanceToCenter = point.getDistance(obstacleCenter);
        var vectorToObstacleCenter = obstacleCenter.minus(point);
        var fullAvoidanceRadius = closestObstacle.avoidanceRadius().in(Meters) + this.radius.in(Meters);
        boolean closeToObstacle = closestObstacle.avoidanceRadius().in(Meters) < distanceToCenter;

        // If we're already really close to a spot that is the avoidance radius then don't really move.
        if ((distanceToCenter - closestObstacle.avoidanceRadius().in(Meters)) < 0.01) {
            return point;
        }

        // Move either away or towards the obstacle based on distance to center, and then go to the edge of the avoidance radius.
        Translation2d fromObstacleCenter;
        if (closeToObstacle) {
            var moveAwayVector = vectorToObstacleCenter.unaryMinus();
            fromObstacleCenter = new Translation2d(closestObstacle.avoidanceRadius().in(Meters), moveAwayVector.getAngle());
        } else {
            fromObstacleCenter =  new Translation2d(closestObstacle.avoidanceRadius().in(Meters), vectorToObstacleCenter.getAngle());
        }

        return obstacleCenter.plus(fromObstacleCenter);
    }

    private Translation2d moveAlongCircumference(Translation2d currentPoint, Translation2d targetPoint, IFieldObstacle closestObstacle) {
        var fullAvoidanceRadius = closestObstacle.avoidanceRadius().in(Meters) + this.radius.in(Meters);
        var obstacleCenter = closestObstacle.center();
        var vectorToObstacleCenter = currentPoint.minus(obstacleCenter);
        var vectorToTarget = targetPoint.minus(obstacleCenter);

        double angleToTarget = vectorToTarget.getAngle().getRadians();
        double angleCurrent = vectorToObstacleCenter.getAngle().getRadians();
        double angleStep = Math.PI / 18;

        double angleDifference = angleToTarget - angleCurrent;
        if (angleDifference > Math.PI) {
            angleDifference -= 2 * Math.PI;
        } else if (angleDifference < -Math.PI) {
            angleDifference += 2 * Math.PI;
        }

        double newAngle = angleCurrent + (angleDifference > 0 ? angleStep : -angleStep);
        return obstacleCenter.plus(new Translation2d(fullAvoidanceRadius, Rotation2d.fromRadians(newAngle)));
    }

    private Trajectory visualizeCircleAsTrajectory(Translation2d center, double radius, int numberOfSteps) {
        var wpiStates = new ArrayList<Trajectory.State>();
        double angleStep = 2 * Math.PI / numberOfSteps;

        for (int i = 0; i < numberOfSteps; i++) {
            double angle = i * angleStep;
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            Pose2d pose = new Pose2d(x, y, new Rotation2d(angle));
            Trajectory.State state = new Trajectory.State();
            state.poseMeters = pose;
            wpiStates.add(state);
        }

        return new Trajectory(wpiStates);
    }
}
