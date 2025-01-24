package xbot.common.subsystems.vision;

import edu.wpi.first.math.geometry.Pose3d;
import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

import static org.junit.Assert.assertEquals;

public class AprilTagVisionSubsystemTest extends BaseCommonLibTest {
    @Test
    public void testBasicOperation() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        assertEquals(1, subsystem.getCameraCount());
        assertEquals(1, subsystem.io.length);
        subsystem.refreshDataFrame();
        subsystem.periodic();
        assertEquals(0, subsystem.getAllPoseObservations().size());
    }

    @Test
    public void testHandleResult() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();

        var io = (MockAprilTagVisionIO)subsystem.io[0];
        io.poseObservations = new AprilTagVisionIO.PoseObservation[] {
                new AprilTagVisionIO.PoseObservation(1, new Pose3d(), 0, 2, 0, AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(2, new Pose3d(), 0, 2, 0, AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(3, new Pose3d(), 0, 2, 0, AprilTagVisionIO.PoseObservationType.PHOTONVISION)
        };
        subsystem.refreshDataFrame();
        subsystem.periodic();

        assertEquals(3, subsystem.getAllPoseObservations().size());

        io.poseObservations = new AprilTagVisionIO.PoseObservation[0];

        subsystem.refreshDataFrame();
        subsystem.periodic();

        assertEquals(0, subsystem.getAllPoseObservations().size());

    }
}
