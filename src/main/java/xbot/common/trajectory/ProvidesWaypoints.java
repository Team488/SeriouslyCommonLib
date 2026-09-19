package xbot.common.trajectory;

import org.wpilib.math.geometry.Pose2d;

import java.util.List;

public interface ProvidesWaypoints {
    public List<XbotSwervePoint> generatePath(Pose2d start, Pose2d end);
}
