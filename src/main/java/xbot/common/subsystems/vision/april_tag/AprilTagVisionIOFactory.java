package xbot.common.subsystems.vision.april_tag;

import edu.wpi.first.math.geometry.Transform3d;

public interface AprilTagVisionIOFactory {
    AprilTagVisionIO create(String name, Transform3d robotToCamera);
}
