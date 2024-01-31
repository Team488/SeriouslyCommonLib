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

    /**
     * Adds an obstacle to the field. If the obstacle intersects with any existing obstacles, it will be combined with
     * the existing obstacle into a single larger obstacle with a bounding box that fully contains both obstacles.
     * This should be the only method used to add new obstacles; otherwise obstacles could intersect.
     * @param o The obstacle to add.
     */
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

    /**
     * Generates a path from the current position to the target point, avoiding known obstacles.
     * Very generally, it will recursively solve paths nearby the target, and then step foward to approach the target.
     * @param currentPose Starting point
     * @param targetPoint Terminating Point
     * @return A List of XbotSwervePoints that can be followed to reach the destination without collision.
     */
    public List<XbotSwervePoint> generatePath(Pose2d currentPose, XbotSwervePoint targetPoint) {
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

        XbotSwervePoint specialFinalPoint = null;

        for (Obstacle o : obstacles) {
            o.resetCorners();
            // Later put in more checks about starting inside an obstacle

            if (o.contains(currentSource.getX(), currentSource.getY())) {
                log.info("Robot is currently inside obstacle" + o.name +". Shifting for calculations.");
                // our original robot pose is inside, and needs to shift.
                Translation2d slidPoint = o.movePointOutsideOfBounds(currentSource);
                currentSource = slidPoint;

                var specialStartingPoint = XbotSwervePoint.createXbotSwervePoint(
                        slidPoint,
                        targetPoint.getRotation2d(),
                        10);
                path.add(specialStartingPoint);
            }

            if (o.contains(targetPoint.getTranslation2d().getX(), targetPoint.getTranslation2d().getY())) {
                log.info("Target is currently inside obstacle" + o.name + ".");
                log.info("Adding an interstitial waypoint just outside obstacle" + o.name + ".");
                // our target is inside - we need to change our focalPoint and save this one.
                Translation2d slidPoint = o.movePointOutsideOfBounds(targetPoint.getTranslation2d());
                // create a new focal point that exactly mimics the final point, except it is out of bounds

                specialFinalPoint = ultimateTarget;

                // For the rest of the logic, consider this "legal" point to be the final point.
                ultimateTarget = XbotSwervePoint.createXbotSwervePoint(
                        slidPoint,
                        targetPoint.getRotation2d(),
                        10);
                currentTarget = ultimateTarget;
            }
        }

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

                    if (specialFinalPoint != null) {
                        path.add(specialFinalPoint);
                    }

                    completed = true;
                }
            }

        }
        return path;
    }
}