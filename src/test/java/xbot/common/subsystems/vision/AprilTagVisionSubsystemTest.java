package xbot.common.subsystems.vision;

import edu.wpi.first.math.geometry.Pose3d;
import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

public class AprilTagVisionSubsystemTest extends BaseCommonLibTest {
    @Test
    public void testBasicOperation() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var visionConsumer = (MockVisionConsumer) this.getInjectorComponent().getAprilTagVisionConsumer();
        assertEquals(1, subsystem.getCameraCount());
        assertEquals(1, subsystem.io.length);
        assertEquals(1, subsystem.inputs.length);
        subsystem.refreshDataFrame();
        subsystem.periodic();
        assertEquals(0, visionConsumer.posesReceived.size());
    }

    @Test
    public void testHandleResult() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var visionConsumer = (MockVisionConsumer) this.getInjectorComponent().getAprilTagVisionConsumer();

        subsystem.refreshDataFrame();
        var inputs = (AprilTagVisionIO.VisionIOInputs)subsystem.inputs[0];
        inputs.poseObservations = new AprilTagVisionIO.PoseObservation[] {
                new AprilTagVisionIO.PoseObservation(1, new Pose3d(), 0, 2, 0, AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(2, new Pose3d(), 0, 2, 0, AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(3, new Pose3d(), 0, 2, 0, AprilTagVisionIO.PoseObservationType.PHOTONVISION)
        };

        subsystem.periodic();
        assertEquals(3, visionConsumer.posesReceived.size());
    }
}
