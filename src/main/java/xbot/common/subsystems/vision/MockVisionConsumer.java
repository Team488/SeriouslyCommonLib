package xbot.common.subsystems.vision;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.List;

/**
 * A mock vision consumer that stores the poses it receives.
 */
@Singleton
public class MockVisionConsumer implements AprilTagVisionSubsystem.VisionConsumer {
    public final List<Pose2d> posesReceived = new LinkedList<>();

    @Inject
    public MockVisionConsumer() {
    }

    @Override
    public void acceptVisionPose(Pose2d visionRobotPoseMeters, double timestampSeconds, Matrix<N3, N1> visionMeasurementStdDevs) {
        posesReceived.add(visionRobotPoseMeters);
    }
}
