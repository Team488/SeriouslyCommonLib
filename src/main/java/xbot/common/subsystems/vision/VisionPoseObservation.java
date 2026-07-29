package xbot.common.subsystems.vision;

import org.wpilib.math.Matrix;
import org.wpilib.math.geometry.Pose2d;
import org.wpilib.math.numbers.N1;
import org.wpilib.math.numbers.N3;

public record VisionPoseObservation(Pose2d visionRobotPoseMeters,
                                    double timestampSeconds,
                                    Matrix<N3, N1> visionMeasurementStdDevs) {
}
