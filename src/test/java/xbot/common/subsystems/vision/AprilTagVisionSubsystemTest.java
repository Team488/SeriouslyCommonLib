package xbot.common.subsystems.vision;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import org.wpilib.math.geometry.Pose3d;
import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.math.geometry.Transform3d;
import org.wpilib.math.geometry.Translation3d;

import xbot.common.injection.BaseCommonLibTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AprilTagVisionSubsystemTest extends BaseCommonLibTest {
    @Test
    public void testBasicOperation() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var registry = this.getInjectorComponent().dataFrameRegistry();

        assertEquals(1, subsystem.getCameraCount());
        assertEquals(1, subsystem.io.length);
        registry.refreshAll();
        subsystem.periodic();
        assertEquals(0, subsystem.getAllPoseObservations().size());
    }

    @Test
    public void testHandleResult() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var registry = this.getInjectorComponent().dataFrameRegistry();

        var io = (MockAprilTagVisionIO) subsystem.io[0];
        io.poseObservations = new AprilTagVisionIO.PoseObservation[] {
                new AprilTagVisionIO.PoseObservation(1, new Pose3d(new Translation3d(0.1, 0.1, 0.1), new Rotation3d()), 0, 2, 2, 0.02, 0.02, 0.6,
                        AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(2, new Pose3d(new Translation3d(0.1, 0.1, 0.1), new Rotation3d()), 0, 2, 2, 0.02, 0.02, 0.6,
                        AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(3, new Pose3d(new Translation3d(0.1, 0.1, 0.1), new Rotation3d()), 0, 2, 2, 0.02, 0.02, 0.6,
                        AprilTagVisionIO.PoseObservationType.PHOTONVISION) };
        io.tagIds = new int[] { 1, 2, 3 };
        registry.refreshAll();
        subsystem.periodic();

        assertEquals(3, subsystem.getAllPoseObservations().size());
        assertTrue(subsystem.tagVisibleByAnyCamera(1));
        assertFalse(subsystem.tagVisibleByAnyCamera(5));
        assertEquals(Set.of(0), subsystem.camerasWithTagVisible(1));

        io.poseObservations = new AprilTagVisionIO.PoseObservation[0];

        registry.refreshAll();
        subsystem.periodic();

        assertEquals(0, subsystem.getAllPoseObservations().size());
    }

    private static AprilTagVisionIO.PoseObservation observation(double x, double y, double z, double ambiguity, int tagCount, double distance) {
        return new AprilTagVisionIO.PoseObservation(1, new Pose3d(new Translation3d(x, y, z), new Rotation3d()), ambiguity, tagCount, distance, 0.02, 0.02, 0.6,
                AprilTagVisionIO.PoseObservationType.PHOTONVISION);
    }

    @Test
    public void testRejectsBadObservations() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var registry = this.getInjectorComponent().dataFrameRegistry();

        var io = (MockAprilTagVisionIO) subsystem.io[0];
        io.poseObservations = new AprilTagVisionIO.PoseObservation[] { observation(3, 3, 0, 0.1, 1, 2.0), observation(3, 3, 0, 0.1, 0, 2.0),
                observation(3, 3, 0, 0.5, 1, 2.0), observation(3, 3, 0, 0.5, 2, 2.0), observation(3, 3, 1.0, 0.1, 2, 2.0), observation(-1.0, 3, 0, 0.1, 2, 2.0),
                observation(3, -1.0, 0, 0.1, 2, 2.0), observation(-0.2, 3, 0, 0.1, 2, 2.0), observation(3, 3, 0, 0.1, 2, 0.2),
                observation(3, 3, 0, 0.1, 1, 4.5), observation(3, 3, 0, 0.1, 2, 4.5), observation(3, 3, 0, 0.1, 2, 5.5) };
        registry.refreshAll();
        subsystem.periodic();

        assertEquals(List.of("ACCEPTED", "NO_TAGS", "HIGH_AMBIGUITY", "ACCEPTED", "Z_ERROR", "OUT_OF_BOUNDS", "OUT_OF_BOUNDS", "ACCEPTED", "TOO_CLOSE",
                "TOO_FAR", "ACCEPTED", "TOO_FAR"), subsystem.cameraHelpers[0].getRejectionReasons());
        assertEquals(4, subsystem.getAllPoseObservations().size());
        assertEquals(8, subsystem.cameraHelpers[0].getRobotPosesRejected().size());
    }

    @Test
    public void testHandleTargetObservations() {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var registry = this.getInjectorComponent().dataFrameRegistry();

        var io = (MockAprilTagVisionIO) subsystem.io[0];
        io.latestTargetObservation = new AprilTagVisionIO.TargetObservation(0, 1, new Rotation2d(), new Rotation2d(), new Transform3d(), 0.2, false);
        io.targetObservations = new AprilTagVisionIO.TargetObservation[] {
                new AprilTagVisionIO.TargetObservation(0, 1, new Rotation2d(), new Rotation2d(), new Transform3d(), 0.2, false),
                new AprilTagVisionIO.TargetObservation(0, 2, new Rotation2d(), new Rotation2d(), new Transform3d(), 0.2, false),
                new AprilTagVisionIO.TargetObservation(0, 3, new Rotation2d(), new Rotation2d(), new Transform3d(), 0.2, false) };
        io.tagIds = new int[] { 1, 2, 3 };
        registry.refreshAll();
        subsystem.periodic();

        assertEquals(1, subsystem.getBestTargetId(0).getAsInt());
        assertTrue(subsystem.tagVisibleByAnyCamera(1));
        assertFalse(subsystem.tagVisibleByAnyCamera(5));
        assertEquals(Set.of(0), subsystem.camerasWithTagVisible(1));
        assertEquals(0, subsystem.getTargetX(0, 2).get().getDegrees(), 0.001);
        assertTrue(subsystem.getTargetObservation(0, 2).isPresent());
        assertFalse(subsystem.getTargetObservation(0, 5).isPresent());
    }
}
