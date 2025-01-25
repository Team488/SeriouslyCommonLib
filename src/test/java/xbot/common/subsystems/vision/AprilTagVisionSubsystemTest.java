package xbot.common.subsystems.vision;

import edu.wpi.first.math.geometry.Pose3d;
import org.junit.Test;
import xbot.common.injection.BaseCommonLibTest;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        io.tagIds = new int[] {1, 2, 3};
        subsystem.refreshDataFrame();
        subsystem.periodic();

        assertEquals(3, subsystem.getAllPoseObservations().size());
        assertTrue(subsystem.tagVisibleByAnyCamera(1));
        assertFalse(subsystem.tagVisibleByAnyCamera(5));
        assertEquals(Set.of(0), subsystem.camerasWithTagVisible(1));

        io.poseObservations = new AprilTagVisionIO.PoseObservation[0];

        subsystem.refreshDataFrame();
        subsystem.periodic();

        assertEquals(0, subsystem.getAllPoseObservations().size());
    }
}
