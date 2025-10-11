package xbot.common.subsystems.vision;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

public record VisionPoseObservation(Pose2d visionRobotPoseMeters,
                                    double timestampSeconds,
                                    Matrix<N3, N1> visionMeasurementStdDevs,
                                    double observationAmbiguity,
                                    int tagCount
                                    ) {
}
