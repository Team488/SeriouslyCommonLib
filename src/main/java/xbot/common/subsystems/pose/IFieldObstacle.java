package xbot.common.subsystems.pose;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.Distance;

public interface IFieldObstacle {
    /**
     * Returns this obstacle can still be moved through, but is slower terrain or more dangerous terrain to move through.
     *
     * @return Whether the obstacle matches the description above for tough terrain.
     */
    public boolean isToughTerrain();

    /**
     * Returns the center of the obstacle to help determine closest obstacle.
     *
     * @return A translation 2d for where the center of this obstacle
     */
    public Translation2d center();

    /**
     * What's the radius that we'll attempt to avoid when we need to move away from the obstacle.
     *
     * @return Whether the obstacle matches the description above for tough terrain.
     */
    public Distance avoidanceRadius();

    /**
     * Whether the described robot path will collide with this field obstacle or not.
     *
     * @param start The start of the path.
     * @param end The end of the path.
     * @param robotRadius The radius we should use to determine whether the robot can make the path through this obstacle.
     *
     * @return Whether the path will work for this obstacle.
     */
    public boolean doesRobotPathIntersect(Translation2d start, Translation2d end, Distance robotRadius);
}
