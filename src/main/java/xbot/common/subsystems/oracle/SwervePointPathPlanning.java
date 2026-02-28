package xbot.common.subsystems.oracle;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.units.measure.Distance;
import static edu.wpi.first.units.Units.Meters;
import edu.wpi.first.wpilibj.MockPowerDistributionPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static Logger log = LogManager.getLogger(SwervePointPathPlanning.class);

    @Inject
    public SwervePointPathPlanning(ObstacleMap obstacleMap, GameField gameField, XSwerveDriveElectricalContract electrical_contract) {
        this.obstacleMap = obstacleMap;
        this.gameField = gameField;
        this.radius = electrical_contract.getRadiusOfRobot();
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
        var closestObstacle = this.obstacleMap.closestObstacle(startingPose, false).orElseThrow();

        Translation2d tangentPoint = null;
        // If we are currently in a rough terrain then it doesn't matter.
        if (this.obstacleMap.doesRobotPathIntersect(start, start, false)) {
            // If we're inside the routing circle and there is a collision, we need to first back out to the routing circle.
            Translation2d direction = start.minus(closestObstacle.center());
            tangentPoint = closestObstacle.center().plus(direction.times(closestObstacle.avoidanceRadius().in(Meters) / direction.getNorm()));
            swervePoints.add(new XbotSwervePoint(new Pose2d(tangentPoint, startingPose.getRotation()), 10));
        } else {
            // otherwise, we're outside the routing circle, and we need to first move to the tangent point.
            tangentPoint = findClosestTangentPoint(start, end, closestObstacle);
            swervePoints.add(new XbotSwervePoint(new Pose2d(tangentPoint, endingPose.getRotation()), 0.001));
        }

        int escape = 0;
        while (this.obstacleMap.doesRobotPathIntersect(tangentPoint, end, allowToughTerrain)) {
            escape++;
            tangentPoint = moveAlongCircumference(tangentPoint, end, 0.25, closestObstacle);
            swervePoints.add(new XbotSwervePoint(new Pose2d(tangentPoint, endingPose.getRotation()), 10));
            if (escape > 100) {
                log.warn("Infinite loop detected in generateSwervePoints, breaking out!");
                break;
            }
        }

        swervePoints.add(new XbotSwervePoint(endingPose, 10));
        return swervePoints;
    }

    // Note - the math in this class is AI generated and has not been fully verified.
    // Some tests have been run, but it needs to be more hardened for robotics (e.g. protection against division by zero)
    // TODO: additional protections
    private Translation2d findClosestTangentPoint(Translation2d point, Translation2d endPoint, IFieldObstacle closestObstacle) {
        double cx = closestObstacle.center().getX();
        double cy = closestObstacle.center().getY();
        double r = radius.in(Meters);
        double px = point.getX();
        double py = point.getY();

        // Distance from circle center to external point
        double dx = px - cx;
        double dy = py - cy;
        double dSq = dx * dx + dy * dy;
        double d = Math.sqrt(dSq);

        // Check for cases like already on the circle or inside the circle
        if ((d < 1e-12) || (d<r) || (Math.abs(d - r) < 1e-12)) {
            return point;
        }

        // Otherwise, there are two tangents.
        // 1. Calculate a = r^2 / d^2
        double a = (r * r) / (dSq);

        // 2. Point M = C + a*(P - C)
        double mx = cx + a * dx;
        double my = cy + a * dy;

        // 3. Vector w = ( - (y1 - y0), x1 - x0 ) = perpendicular to CP
        //    In our notation, w = ( -dy, dx )
        double wx = -dy;
        double wy = dx;

        // 4. Distance from M to each tangent point = b = (r/d) * sqrt(d^2 - r^2)
        double h = Math.sqrt(dSq - r * r);
        double b = (r / d) * h;

        // Length of w is d; normalize and scale by b
        double wxScaled = wx * (b / d);
        double wyScaled = wy * (b / d);

        // 5. The two tangent points T1 and T2
        double t1x = mx + wxScaled;
        double t1y = my + wyScaled;
        double t2x = mx - wxScaled;
        double t2y = my - wyScaled;

        Translation2d tangentPoint1 = new Translation2d(t1x, t1y);
        Translation2d tangentPoint2 = new Translation2d(t2x, t2y);

        // Choose the tangent point closest to the end point
        double distanceToEnd1 = tangentPoint1.getDistance(endPoint);
        double distanceToEnd2 = tangentPoint2.getDistance(endPoint);

        return distanceToEnd1 < distanceToEnd2 ? tangentPoint1 : tangentPoint2;
    }

    private Translation2d moveAlongCircumference(Translation2d currentPoint, Translation2d targetPoint, double distance, IFieldObstacle closestObstacle) {
        double angleToTarget = Math.atan2(targetPoint.getY() - closestObstacle.center().getY(), targetPoint.getX() - closestObstacle.center().getX());
        double angleCurrent = Math.atan2(currentPoint.getY() - closestObstacle.center().getY(), currentPoint.getX() - closestObstacle.center().getX());
        double angleStep = distance / radius.in(Meters);

        double angleDifference = angleToTarget - angleCurrent;
        if (angleDifference > Math.PI) {
            angleDifference -= 2 * Math.PI;
        } else if (angleDifference < -Math.PI) {
            angleDifference += 2 * Math.PI;
        }

        double newAngle = angleCurrent + (angleDifference > 0 ? angleStep : -angleStep);
        return new Translation2d(closestObstacle.center().getX() + radius.in(Meters) * Math.cos(newAngle),
                                 closestObstacle.center().getY() + radius.in(Meters) * Math.sin(newAngle));
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
