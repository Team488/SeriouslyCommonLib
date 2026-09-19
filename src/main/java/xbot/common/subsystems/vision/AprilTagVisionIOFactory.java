package xbot.common.subsystems.vision;

import org.wpilib.math.geometry.Transform3d;

public interface AprilTagVisionIOFactory {
    AprilTagVisionIO create(String name, Transform3d robotToCamera);
}
