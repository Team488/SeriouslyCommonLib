package xbot.common.trajectory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.math.FieldPose;
import xbot.common.math.XYPair;
import xbot.common.subsystems.drive.RabbitPoint;
import xbot.common.subsystems.drive.RabbitPoint.PointTerminatingType;
import xbot.common.subsystems.drive.RabbitPoint.PointType;

/**
 * Represents a simple version of the field, with Axis-Aligned Bounding Boxes representing no-go zones due to obstacles.
 */
public class LowResField {

    List<Obstacle> obstacles;
    Logger log = LogManager.getLogger(LowResField.class);

    public LowResField() {
        obstacles = new ArrayList<Obstacle>();
    }

    public void addObstacle(Obstacle o) {
        obstacles.add(o);
    }

    public List<Obstacle> getObstacles() {
        return new ArrayList<Obstacle>(obstacles);
    }

    /**
     * Generates a path between any two points on the field, avoiding known obstacles. Generated
     * interstitial points will be Position-Only. Final target point will be unchanged.
     * @param robotPose The robot's current position
     * @param targetPoint The robot's destination. This better be outside the bounding boxes!
     * @return List of waypoints, followed by the target point.
     */
    public List<RabbitPoint> generatePath(FieldPose robotPose, RabbitPoint targetPoint) {
        // We may need to modify the robot pose, so let's clone it to avoid any
        // side effects.
        robotPose = robotPose.clone();
        var path = new ArrayList<RabbitPoint>();
        var rabbitStack = new Stack<RabbitPoint>();
        var sortedObstacles = new TreeMap<Double, Obstacle>();
        // Start from the final point, generate a path backwards.
        // Check to see if there are any intersections.

        // TODO: some part of this algorithm needs to be modified in case the robot
        // or the target is currently inside a bounding box.
        // In both cases, it would probalby be best to project those points slightly outside the
        // bounding boxes, then continue as usual.

        // Possible approaches if already inside:
        // find the distance to each of the line segments, and keep the smallest one
        // add that distance, plus 1%, in the proper direction

        // For now, I recommend keeping the points outside.

        RabbitPoint focalPoint = targetPoint;
        boolean collision = true;

        for (Obstacle o : obstacles) {
            o.resetCorners();
            // Check to see if either the robot position or the final destination are
            // inside, and if so, modify them for the purposes of all following calculations.
            // We can directly change the robot pose

            if (o.contains(robotPose.getPoint().x, robotPose.getPoint().y)) {
                log.info("Robot is currently inside obstacle" + o.name +". Shifting for calculations.");
                // our original robot pose is inside, and needs to shift.
                XYPair slidPoint = o.movePointOutsideOfBounds(robotPose.getPoint());
                robotPose = new FieldPose(slidPoint, robotPose.getHeading());
            }

            if (o.contains(targetPoint.pose.getPoint().x, targetPoint.pose.getPoint().y)) {
                log.info("Target is currently inside obstacle" + o.name + ".");
                log.info("Adding an interstitial waypoint just outside obstacle" + o.name + ".");
                // our target is inside - we need to change our focalPoint and save this one.
                XYPair slidPoint = o.movePointOutsideOfBounds(targetPoint.pose.getPoint());
                rabbitStack.push(targetPoint);
                // create a new focal point that exactly mimics the final point, except it is out of bounds
                focalPoint = new RabbitPoint(
                        new FieldPose(slidPoint, targetPoint.pose.getHeading()),
                        targetPoint.pointType,
                        PointTerminatingType.Continue,
                        targetPoint.driveStyle);
            }
        }

        // Iterate until no more collisions.
        int escape = 0;
        while (collision) {
            // The robot needs to be safe - anywhere we perform a while loop, we better
            // have some sort of emergency escape.
            escape++;
            if (escape > 20) {
                log.warn("Iterated more than 20 times! Something has gone wrong! Escaping this loop.");
                return new ArrayList<RabbitPoint>(Arrays.asList(targetPoint));
            }

            // Set collisions to false. Now, unless we find a collision, this will be the last time through the loop.
            collision = false;

            // Sort all the obstacles by distance using a TreeMap. A cool feature of the TreeMap
            // is that it automatically sorts the keys if they are sortable. That way, we can
            // check for collisions from the closest obstacle to the furthest, and if we see a collision,
            // we can immediately ignore all others.
            sortedObstacles.clear();
            for (Obstacle o : obstacles) {
                double distance = o.getDistanceToCenter(focalPoint.pose.getPoint());
                sortedObstacles.put(distance, o);
            }
            // Now, time to look for collisions.
            for (Double d : sortedObstacles.keySet()) {
                Obstacle o = sortedObstacles.get(d);

                // First, check to see if the line between the robot and the focal point has any collisions.
                if (o.intersectsLine(robotPose.getPoint().x, robotPose.getPoint().y, focalPoint.pose.getPoint().x, focalPoint.pose.getPoint().y)) {
                    // The line collidies with this obstacle! Set collision to true so we continue to iterate.
                    collision = true;
                    log.info("Projected path currently collides with " + o.name + ". Creating a new safe point.");
                    XYPair pointToSearchFrom = new XYPair();
                    // We want to find the closest corner of the bounding box that will create an optimal path.
                    // If a line crosses a rectangle, then in nearly all cases, there will be two intersection points.
                    // We find those, and average them together. Then, we get the corner closest to that average point.
                    XYPair averageIntersection = o.getIntersectionAveragePoint(robotPose.getPoint(), focalPoint.pose.getPoint());
                    // However, if the line crosses parallel lines (e.g. left and right, or top and bottom)
                    // this method doesn't work, as the average point will just be the exact center of the box,
                    // so it's a coin flip as to which corner is chosen.
                    // So, if the averageIntersection point's X or Y is the same as the center of the obstacle,
                    // then we can go with a much simpler method - just take the corner closest to the focal point.
                    if (o.doesPointLieAlongMidlines(averageIntersection)) {
                        pointToSearchFrom = targetPoint.pose.getPoint();
                    } else {
                        pointToSearchFrom = averageIntersection;
                    }

                    XYPair nearestCorner = o.getClosestCornerToPoint(pointToSearchFrom);

                    // Now to transform that x,y coordinate into a waypoint. It becomes a RabbitPoint,
                    // using PositionOnly so that we drive straight there (orientation doesn't matter).
                    RabbitPoint cornerPoint =
                            new RabbitPoint(new FieldPose(nearestCorner.x, nearestCorner.y, 0), PointType.PositionOnly, PointTerminatingType.Continue);
                    // Our current focal point needs to be saved in the final list.
                    log.info("New safe point created: " + cornerPoint.pose.toString());
                    rabbitStack.add(focalPoint);
                    // Change the focal point to the new corner point, and check for collisions again.
                    focalPoint = cornerPoint;
                    break;
                }
            }
        }

        // The iteration is complete, so it's time to build up the path.
        // We don't need to add the robot's current position, so next we add the Focal Point.
        // If there we no collisions, the focal point will be the targetPoint.
        log.info("Path clear. Adding goal point.");
        path.add(focalPoint);
        while (rabbitStack.size() > 0) {
            // If there were collisions, then we need to use the rabbitStack. The top contains generated waypoints, and
            // as we go down the stack, we get to generated waypoints further from the robot, finally ending in
            // the targetPoint.
            path.add(rabbitStack.pop());
        }

        return path;
    }
}