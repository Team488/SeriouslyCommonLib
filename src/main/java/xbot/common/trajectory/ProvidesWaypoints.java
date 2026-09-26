package xbot.common.trajectory;

import java.util.List;

import org.wpilib.math.geometry.Pose2d;

public interface ProvidesWaypoints {
    public List<XbotSwervePoint> generatePath(Pose2d start, Pose2d end);
}
