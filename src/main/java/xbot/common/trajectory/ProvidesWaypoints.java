package xbot.common.trajectory;

import java.util.List;

import org.wpilib.math.geometry.Pose2d;

/**
 * @deprecated Path planning and path following are moving to PathPlanner. This will be removed in a future release.
 */
@Deprecated
public interface ProvidesWaypoints {
    public List<XbotSwervePoint> generatePath(Pose2d start, Pose2d end);
}
