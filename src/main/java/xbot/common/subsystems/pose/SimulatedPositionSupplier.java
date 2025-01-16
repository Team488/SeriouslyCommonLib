package xbot.common.subsystems.pose;

import edu.wpi.first.math.geometry.Pose2d;

public interface SimulatedPositionSupplier {
    public Pose2d getGroundTruthPose();
}
