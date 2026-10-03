package xbot.common.subsystems.pose;

import org.wpilib.math.geometry.Pose2d;

public interface SimulatedPositionSupplier {
    public Pose2d getGroundTruthPose();
}
