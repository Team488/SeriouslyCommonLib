package xbot.common.trajectory;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

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
        Obstacle potentialObstacleToRemove = null;
        for (Obstacle existingObstacle : obstacles) {
            if (existingObstacle.intersects(o) || existingObstacle.contains(o) || o.contains(existingObstacle)) {
                log.info("Collides with existing obstacle. Combining.");
                existingObstacle.absorbObstacle(o);
                potentialObstacleToRemove = existingObstacle;
                break;
            }
        }

        if (potentialObstacleToRemove != null) {
            obstacles.remove(potentialObstacleToRemove);
            addObstacle(potentialObstacleToRemove);
        } else {
            log.info("Does not collide with existing obstacle. Adding.");
            obstacles.add(o);
        }
    }

    public List<Obstacle> getObstacles() {
        return new ArrayList<Obstacle>(obstacles);
    }

    public void squashObstacles() {
        // Check if any obstacles are inside other obstacles.
        // If so, combine them into a new obstacle that fully contains the previous two.


    }

    public List<XbotSwervePoint> generatePathFrontToBack(Pose2d currentPose, XbotSwervePoint targetPoint) {
        // New attempt at a pathfinding algorithm that maybe a little more resilient. The original algorithm had
        // two issues:
        // - It worked backwards, which was a little unintuitive, but not strictly a problem.
        // - It didn't reliably check for new collisions when generating intermediate points.
        // This approach should solve both those issues. Basic idea:
        // - Check for collisions between robot and target.
        // - If collision, set the nearest corner of that obstacle as new target.
        // - Check to make sure that there are no obstacles between you and that corner - if so, repeat the above.
        // - Once satisfied there are no obstacles, advance to the corner and repeat the process of trying to get
        //   to the ultimate target.

        // General code structure::
        // 1) Save the initial target as the ultimate target; we'll need it later.
        //      a) initialize the current target from the ultimate target.
        //      b) initialize an empty list of swerve points.
        // 2) Create a Stack of XbotSwervePoints, which will be used to help us deal with multiple intermediate points.
        // ===== Main loop =====
        // 3) Raycast to the center of each obstacle, recording distance to the first intersection with the bounding box.
        //      a) Put the obstacles into a TreeMap sorted by that distance.
        // 4) Raycast from the current position to the destination, and iterate through each of the obstacles to see
        //    if there is a collision.
        //      a) If no collision, AND the current target is the ultimate target, we are done.
        //         Add the current target to the list, and return.
        //      b) If no collision, and the stack isn't empty, fully pop the stack into the list and set
        //         the current target to the ultimate target, and the current position to the last point in the list.
        //         Loop back to step (3).
        //      c) If collision, get the nearest legal corner, put it on the stack, and set it as the current target.
        //         Loop back to step (3).

        // 1) Save the initial target as the ultimate target; we'll need it later.
        XbotSwervePoint ultimateTarget = targetPoint;
        //      a) initialize the current target from the ultimate target.
        XbotSwervePoint currentTarget = ultimateTarget;
        Translation2d currentSource = currentPose.getTranslation();
        //      b) initialize an empty list of swerve points.
        List<XbotSwervePoint> path = new ArrayList<XbotSwervePoint>();
        // 2) Create a Stack of XbotSwervePoints, which will be used to help us deal with multiple intermediate points.
        Stack<XbotSwervePoint> swervePointStack = new Stack<XbotSwervePoint>();

        for (Obstacle o : obstacles) {
            o.resetCorners();
            // Later put in more checks about starting inside an obstacle
        }

        // ===== Main loop =====
        // 3) Raycast to the center of each obstacle, recording distance to the first intersection with the bounding box.
        //      a) Put the obstacles into a TreeMap sorted by that distance.
        TreeMap<Double, Obstacle> sortedObstacles = new TreeMap<Double, Obstacle>();

        boolean completed = false;
        int escape = 0;
        while (!completed && escape < 20) {

            log.info("Searching from CurrentSource" + currentSource.toString()
                    + " to CurrentTarget" + currentTarget.getTranslation2d().toString());

            sortedObstacles.clear();
            escape++;
            for (Obstacle o : obstacles) {
                double distance = o.findClosestPointOnPerimeterToPoint(currentSource);
                sortedObstacles.put(distance, o);
            }

            boolean collision = false;

            // 4) Raycast from the current position to the destination, and iterate through each of the obstacles to see
            //    if there is a collision.
            for (Double d : sortedObstacles.keySet()) {
                Obstacle o = sortedObstacles.get(d);
                log.info("Checking obstacle " + o.name + " at distance " + d.toString() + " from source.");

                if (o.intersectsLine(currentSource.getX(), currentSource.getY(),
                        currentTarget.getTranslation2d().getX(), currentTarget.getTranslation2d().getY())) {
                    log.info("Collision detected.");
                    collision = true;
                    // 4c) If collision, get the nearest legal corner, put it on the stack, and set it as the current target.
                    //         Loop back to step (3).
                    Translation2d pointToSearchFrom = new Translation2d();
                    Translation2d averageIntersection = o.getIntersectionAveragePoint(currentSource, currentTarget.getTranslation2d());
                    Obstacle.PointProjectionCombination pointCombination = Obstacle.PointProjectionCombination.NotRelevant;
                    Obstacle.ParallelCrossingType parallelCrossingType = o.getParallelCrossingType(averageIntersection);
                    if (parallelCrossingType != Obstacle.ParallelCrossingType.None) {
                        // Parallel crossings have a number of interesting scenarios.
                        // Scenario 1:
                        // If both points are inside the projection of the obstacle, we can take the closest corner,
                        // BUT we also need to eliminate the other corner on the same side of the obstacle.


                        // Scenario 2:
                        // We need to perform an additional special check. If both start and end are
                        // "outside" the vertical/horizontal projections of the obstacle ,then we have a
                        // "crossing the diagonal" problem. In this case, we need to eliminate the closest
                        // corner.
                        // However, if they are both inside, then we have the typical "just take the closest corner"
                        // approach, and no extra effort is needed.
                        pointCombination = o.getPointProjectionCombination(
                                currentSource, targetPoint.getTranslation2d(), parallelCrossingType);
                    }

                    log.info("Parallel Crossing Type: " + parallelCrossingType.toString());
                    log.info("Point Combination: " + pointCombination.toString());


                    if (parallelCrossingType != Obstacle.ParallelCrossingType.None
                            && pointCombination == Obstacle.PointProjectionCombination.BothOutside) {
                        log.info("Handing diagonal crossing.");
                        // Case for diagonal crossings.
                        // We eliminate the closest corner to the source,
                        // then search again to find a better point. That's the one we add to the stack and set
                        // our new target to. (The reason to search twice is the first search will mark the corner
                        // as "used up.")
                        var nearestCorner = o.getClosestCornerToPoint(currentSource);
                        nearestCorner = o.getClosestCornerToPoint(currentSource);
                        log.info("Chosen corner:" + nearestCorner.toString());
                        XbotSwervePoint cornerPoint = XbotSwervePoint.createXbotSwervePoint(
                                nearestCorner,
                                currentTarget.getRotation2d(),
                                10
                        );
                        swervePointStack.push(cornerPoint);
                        currentTarget = cornerPoint;
                    } else if (parallelCrossingType != Obstacle.ParallelCrossingType.None
                            && pointCombination == Obstacle.PointProjectionCombination.BothInside) {
                        log.info("Handing straight-across crossing.");
                        // Case for "straight-across" crossings.
                        // We search twice from the average intersection, but the order of points isn't guaranteed.
                        // Get the one closest to the target first, push that on the stack, then do the other one.
                        var firstArbitraryCorner = o.getClosestCornerToPoint(averageIntersection);
                        var secondArbitraryCorner = o.getClosestCornerToPoint(averageIntersection);
                        Translation2d pointClosestToTarget = null;
                        Translation2d pointClosestToSource = null;
                        if (firstArbitraryCorner.getDistance(targetPoint.getTranslation2d())
                                < secondArbitraryCorner.getDistance(targetPoint.getTranslation2d())) {
                            pointClosestToTarget = firstArbitraryCorner;
                            pointClosestToSource = secondArbitraryCorner;
                        } else {
                            pointClosestToTarget = secondArbitraryCorner;
                            pointClosestToSource = firstArbitraryCorner;
                        }

                        XbotSwervePoint swervePointNearTarget = XbotSwervePoint.createXbotSwervePoint(
                                pointClosestToTarget,
                                currentTarget.getRotation2d(),
                                10
                        );
                        XbotSwervePoint swervePointNearSource = XbotSwervePoint.createXbotSwervePoint(
                                pointClosestToSource,
                                currentTarget.getRotation2d(),
                                10
                        );

                        log.info("Adding point closest to target:" + pointClosestToTarget.toString());
                        log.info("Adding point closest to source:" + pointClosestToSource.toString());

                        swervePointStack.push(swervePointNearTarget);
                        swervePointStack.push(swervePointNearSource);
                        currentTarget = swervePointNearSource;
                    } else if (parallelCrossingType != Obstacle.ParallelCrossingType.None
                            && pointCombination == Obstacle.PointProjectionCombination.FirstInside
                            || pointCombination == Obstacle.PointProjectionCombination.SecondInside) {
                        log.info("Handling mixed case - two parallel lines, one inside.");
                        // This is similar to the straight across, except we only want one point - and that
                        // point should be closer to the "inside" point.
                        var arbitraryCornerA = o.getClosestCornerToPoint(averageIntersection);
                        var arbitraryCornerB = o.getClosestCornerToPoint(averageIntersection);
                        Translation2d pointClosestToInside = null;
                        Translation2d insidePoint = null;

                        // First is Source, Second is Target.
                        if (pointCombination == Obstacle.PointProjectionCombination.FirstInside) {
                            insidePoint = currentSource;
                        } else {
                            insidePoint = targetPoint.getTranslation2d();
                        }

                        // Find which corner is closest to the inside point
                        if (arbitraryCornerA.getDistance(insidePoint)
                                < arbitraryCornerB.getDistance(insidePoint)) {
                            pointClosestToInside = arbitraryCornerA;
                        } else {
                            pointClosestToInside = arbitraryCornerB;
                        }

                        log.info("Adding point closest to 'inside' point:" + pointClosestToInside);

                        XbotSwervePoint swervePointNearInside = XbotSwervePoint.createXbotSwervePoint(
                                pointClosestToInside,
                                currentTarget.getRotation2d(),
                                10
                        );
                        swervePointStack.push(swervePointNearInside);
                        currentTarget = swervePointNearInside;
                    }
                    else {
                        log.info("Handling non-parallel crossing; typical case..");
                        // General, most straightforward case. Get the nearest corner to the average intersection,
                        // and prepare for another loop.
                        var nearestCorner = o.getClosestCornerToPoint(averageIntersection);
                        XbotSwervePoint cornerPoint = XbotSwervePoint.createXbotSwervePoint(
                                nearestCorner,
                                currentTarget.getRotation2d(),
                                10
                        );
                        log.info("Chosen corner:" + nearestCorner.toString());
                        swervePointStack.push(cornerPoint);
                        currentTarget = cornerPoint;
                    }

                    break;
                }
            }

            // We either just collided with something, or checked all obstacles and found nothing.
            // If we didn't collide with anything, we need to check for terminating or updating conditions.
            if (!collision) {
                if (swervePointStack.size() > 0) {
                    // 4b) If no collision, and the stack isn't empty, fully pop the stack into the list and set
                    //         the current target to the ultimate target, and the current position to the last point in the list.
                    //         Loop back to step (3).
                    while (swervePointStack.size() > 0) {
                        path.add(swervePointStack.pop());
                    }
                    log.info("Popped stack into path.");
                    log.info("Setting target to ultimate target");

                    currentTarget = ultimateTarget;
                    currentSource = path.get(path.size() - 1).getTranslation2d();

                    log.info("Set source to last point in path: " + currentSource.toString());
                } else {
                    log.info("No more collisions found; route clear. Adding terminating point and finishing.");
                    // 4a) If no collision, AND the current target is the ultimate target, we are done.
                    //         Add the current target to the list, and return.
                    path.add(currentTarget);
                    completed = true;
                }
            }

        }
        return path;
    }

    /**
     * Generates a path between any two points on the field, avoiding known obstacles. Generated
     * interstitial points will be Position-Only. Final target point will be unchanged.
     * @param currentRobotPose The robot's current position
     * @param targetPoint The robot's destination. This better be outside the bounding boxes!
     * @return List of waypoints, followed by the target point.
     */
    public List<XbotSwervePoint> generatePath(Pose2d currentRobotPose, XbotSwervePoint targetPoint) {
        // We may need to modify the robot pose, so let's clone it to avoid any
        // side effects.
        var freshRobotPose = new Pose2d(currentRobotPose.getTranslation(), currentRobotPose.getRotation());
        var path = new ArrayList<XbotSwervePoint>();
        var swervePointStack = new Stack<XbotSwervePoint>();
        var sortedObstacles = new TreeMap<Double, Obstacle>();
        // Start from the final point, generate a path backwards.
        // Check to see if there are any intersections.

        // TODO: some part of this algorithm needs to be modified in case the robot
        // or the target is currently inside a bounding box.
        // In both cases, it would probably be best to project those points slightly outside the
        // bounding boxes, then continue as usual.

        // Possible approaches if already inside:
        // find the distance to each of the line segments, and keep the smallest one
        // add that distance, plus 1%, in the proper direction

        // For now, I recommend keeping the points outside.

        XbotSwervePoint focalPoint = targetPoint;
        boolean collision = true;

        for (Obstacle o : obstacles) {
            o.resetCorners();
            // Check to see if either the robot position or the final destination are
            // inside, and if so, modify them for the purposes of all following calculations.
            // We can directly change the robot pose

            if (o.contains(freshRobotPose.getTranslation().getX(), freshRobotPose.getTranslation().getY())) {
                log.info("Robot is currently inside obstacle" + o.name +". Shifting for calculations.");
                // our original robot pose is inside, and needs to shift.
                Translation2d slidPoint = o.movePointOutsideOfBounds(freshRobotPose.getTranslation());
                freshRobotPose = new Pose2d(slidPoint, freshRobotPose.getRotation());
            }

            if (o.contains(targetPoint.getTranslation2d().getX(), targetPoint.getTranslation2d().getY())) {
                log.info("Target is currently inside obstacle" + o.name + ".");
                log.info("Adding an interstitial waypoint just outside obstacle" + o.name + ".");
                // our target is inside - we need to change our focalPoint and save this one.
                Translation2d slidPoint = o.movePointOutsideOfBounds(targetPoint.getTranslation2d());
                swervePointStack.push(targetPoint);
                // create a new focal point that exactly mimics the final point, except it is out of bounds

                focalPoint = XbotSwervePoint.createXbotSwervePoint(
                        slidPoint,
                        targetPoint.getRotation2d(),
                        10);
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
                return new ArrayList<XbotSwervePoint>(Arrays.asList(targetPoint));
            }

            // Set collisions to false. Now, unless we find a collision, this will be the last time through the loop.
            collision = false;

            // Sort all the obstacles by distance using a TreeMap. A cool feature of the TreeMap
            // is that it automatically sorts the keys if they are sortable. That way, we can
            // check for collisions from the closest obstacle to the furthest, and if we see a collision,
            // we can immediately ignore all others.
            sortedObstacles.clear();
            for (Obstacle o : obstacles) {
                double distance = o.getDistanceToCenter(focalPoint.getTranslation2d());
                sortedObstacles.put(distance, o);
            }
            // Now, time to look for collisions.
            for (Double d : sortedObstacles.keySet()) {
                Obstacle o = sortedObstacles.get(d);

                // First, check to see if the line between the robot and the focal point has any collisions.
                if (o.intersectsLine(freshRobotPose.getTranslation().getX(), freshRobotPose.getTranslation().getY(),
                        focalPoint.getTranslation2d().getX(), focalPoint.getTranslation2d().getY())) {
                    // The line collidies with this obstacle! Set collision to true so we continue to iterate.
                    collision = true;
                    log.info("Projected path currently collides with " + o.name + ". Creating a new safe point.");
                    Translation2d pointToSearchFrom = new Translation2d();
                    // We want to find the closest corner of the bounding box that will create an optimal path.
                    // If a line crosses a rectangle, then in nearly all cases, there will be two intersection points.
                    // We find those, and average them together. Then, we get the corner closest to that average point.
                    Translation2d averageIntersection = o.getIntersectionAveragePoint(
                            freshRobotPose.getTranslation(), focalPoint.getTranslation2d());
                    // However, if the line crosses parallel lines (e.g. left and right, or top and bottom)
                    // this method doesn't work, as the average point will just be the exact center of the box,
                    // so it's a coin flip as to which corner is chosen.
                    // So, if the averageIntersection point's X or Y is the same as the center of the obstacle,
                    // then we can go with a much simpler method - just take the corner closest to the focal point.

                    boolean bothOutsideAndAcrossMidlines = false;
                    Obstacle.ParallelCrossingType parallelCrossingType = o.getParallelCrossingType(averageIntersection);
                    if (parallelCrossingType != Obstacle.ParallelCrossingType.None) {
                        // We need to perform an additional special check. If both start and end are
                        // "outside" the vertical/horizontal projections of the obstacle ,then we have a
                        // "crossing the diagonal" problem. In this case, we need to eliminate the closest
                        // corner.
                        // However, if they are both inside, then we have the typical "just take the closest corner"
                        // approach, and no extra effort is needed.
                        bothOutsideAndAcrossMidlines = o.getPointProjectionCombination(
                                freshRobotPose.getTranslation(), focalPoint.getTranslation2d(), parallelCrossingType)
                                == Obstacle.PointProjectionCombination.BothOutside;
                        pointToSearchFrom = targetPoint.getTranslation2d();
                    } else {
                        pointToSearchFrom = averageIntersection;
                    }

                    Translation2d nearestCorner = o.getClosestCornerToPoint(pointToSearchFrom);
                    if (bothOutsideAndAcrossMidlines) {
                        // do it again, since the closest corner was just eliminated.
                        nearestCorner = o.getClosestCornerToPoint(pointToSearchFrom);
                    }

                    // Now to transform that x,y coordinate into a waypoint. It becomes a RabbitPoint,
                    // using PositionOnly so that we drive straight there (orientation doesn't matter).
                    XbotSwervePoint cornerPoint = XbotSwervePoint.createXbotSwervePoint(
                            nearestCorner,
                            targetPoint.getRotation2d(),
                            10
                    );
                    // Our current focal point needs to be saved in the final list.
                    log.info("New safe point created: " + cornerPoint.getTranslation2d().toString());
                    swervePointStack.add(focalPoint);
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
        while (swervePointStack.size() > 0) {
            // If there were collisions, then we need to use the rabbitStack. The top contains generated waypoints, and
            // as we go down the stack, we get to generated waypoints further from the robot, finally ending in
            // the targetPoint.
            path.add(swervePointStack.pop());
        }

        return path;
    }
}