package xbot.common.subsystems.vision;

import edu.wpi.first.math.geometry.Transform3d;

public interface AprilTagVisionIOFactory {
    AprilTagVisionIO create(String name, Transform3d robotToCamera);
}
