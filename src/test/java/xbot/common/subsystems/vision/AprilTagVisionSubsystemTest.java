package xbot.common.subsystems.vision;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
                new AprilTagVisionIO.PoseObservation(1, new Pose3d(new Translation3d(0.1, 0.1, 0.1), new Rotation3d()), 0, 2, 0, 0.02, 0.02, 0.6,
                        AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(2, new Pose3d(new Translation3d(0.1, 0.1, 0.1), new Rotation3d()), 0, 2, 0, 0.02, 0.02, 0.6,
                        AprilTagVisionIO.PoseObservationType.PHOTONVISION),
                new AprilTagVisionIO.PoseObservation(3, new Pose3d(new Translation3d(0.1, 0.1, 0.1), new Rotation3d()), 0, 2, 0, 0.02, 0.02, 0.6,
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

    private List<String> rejectionReasonsFor(AprilTagVisionIO.PoseObservation... observations) {
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var io = (MockAprilTagVisionIO) subsystem.io[0];
        io.poseObservations = observations;
        this.getInjectorComponent().dataFrameRegistry().refreshAll();
        subsystem.periodic();
        return subsystem.cameraHelpers[0].getRejectionReasons();
    }

    private void enableOutOfBoundsRejection() {
        this.getInjectorComponent().getAprilTagVisionSubsystem().cameraHelpers[0].rejectOutOfBoundsObservations.set(true);
    }

    static Stream<Arguments> defaultSettingsCases() {
        return Stream.of(Arguments.of("single tag on field", observation(3, 3, 0, 0.1, 1, 2.0), "ACCEPTED"),
                Arguments.of("multi tag on field", observation(3, 3, 0, 0.1, 2, 4.5), "ACCEPTED"),
                Arguments.of("raised by bump", observation(3, 3, 0.3, 0.1, 2, 2.0), "ACCEPTED"),
                Arguments.of("exactly at max z", observation(3, 3, 0.75, 0.1, 2, 2.0), "ACCEPTED"),
                Arguments.of("exactly at min z", observation(3, 3, -0.75, 0.1, 2, 2.0), "ACCEPTED"),
                Arguments.of("high ambiguity is not rejected", observation(3, 3, 0, 0.9, 1, 2.0), "ACCEPTED"),
                Arguments.of("far tag is not rejected", observation(3, 3, 0, 0.1, 1, 8.0), "ACCEPTED"),
                Arguments.of("off field is accepted when bounds check is off", observation(-1.0, 3, 0, 0.1, 2, 2.0), "ACCEPTED"),
                Arguments.of("no tags", observation(3, 3, 0, 0.1, 0, 2.0), "NO_TAGS"),
                Arguments.of("just above max z", observation(3, 3, 0.76, 0.1, 2, 2.0), "Z_ERROR"),
                Arguments.of("above field", observation(3, 3, 1.5, 0.1, 2, 2.0), "Z_ERROR"),
                Arguments.of("below field", observation(3, 3, -1.5, 0.1, 2, 2.0), "Z_ERROR"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("defaultSettingsCases")
    public void testRejectionReason(String description, AprilTagVisionIO.PoseObservation observation, String expectedReason) {
        assertEquals(List.of(expectedReason), rejectionReasonsFor(observation));
    }

    static Stream<Arguments> outOfBoundsCases() {
        return Stream.of(Arguments.of("on field", observation(3, 3, 0, 0.1, 2, 2.0), "ACCEPTED"),
                Arguments.of("just inside the origin corner", observation(0.01, 0.01, 0, 0.1, 2, 2.0), "ACCEPTED"),
                Arguments.of("at origin", observation(0, 0, 0, 0.1, 2, 2.0), "OUT_OF_BOUNDS"),
                Arguments.of("on x = 0 edge", observation(0, 3, 0, 0.1, 2, 2.0), "OUT_OF_BOUNDS"),
                Arguments.of("on y = 0 edge", observation(3, 0, 0, 0.1, 2, 2.0), "OUT_OF_BOUNDS"),
                Arguments.of("past negative x edge", observation(-1.0, 3, 0, 0.1, 2, 2.0), "OUT_OF_BOUNDS"),
                Arguments.of("past positive x edge", observation(100, 3, 0, 0.1, 2, 2.0), "OUT_OF_BOUNDS"),
                Arguments.of("past negative y edge", observation(3, -1.0, 0, 0.1, 2, 2.0), "OUT_OF_BOUNDS"),
                Arguments.of("past positive y edge", observation(3, 100, 0, 0.1, 2, 2.0), "OUT_OF_BOUNDS"));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("outOfBoundsCases")
    public void testOutOfBoundsRejectionWhenEnabled(String description, AprilTagVisionIO.PoseObservation observation, String expectedReason) {
        enableOutOfBoundsRejection();
        assertEquals(List.of(expectedReason), rejectionReasonsFor(observation));
    }

    @Test
    public void testOnlyFirstFailingRuleIsReported() {
        enableOutOfBoundsRejection();
        var noTagsAndAboveField = observation(3, 3, 1.5, 0.1, 0, 2.0);
        var aboveFieldAndOutOfBounds = observation(-1.0, 3, 1.5, 0.1, 2, 2.0);

        assertEquals(List.of("NO_TAGS", "Z_ERROR"), rejectionReasonsFor(noTagsAndAboveField, aboveFieldAndOutOfBounds));
    }

    @Test
    public void testOnlyAcceptedObservationsAreUsed() {
        enableOutOfBoundsRejection();
        var subsystem = this.getInjectorComponent().getAprilTagVisionSubsystem();
        var accepted = observation(3, 3, 0, 0.1, 2, 2.0);

        var reasons = rejectionReasonsFor(accepted, observation(3, 3, 0, 0.1, 0, 2.0), observation(3, 3, 1.5, 0.1, 2, 2.0),
                observation(-1.0, 3, 0, 0.1, 2, 2.0));

        assertEquals(List.of("ACCEPTED", "NO_TAGS", "Z_ERROR", "OUT_OF_BOUNDS"), reasons);
        assertEquals(1, subsystem.getAllPoseObservations().size());
        assertEquals(accepted.pose().toPose2d(), subsystem.getAllPoseObservations().get(0).visionRobotPoseMeters());
        assertEquals(1, subsystem.cameraHelpers[0].getRobotPosesAccepted().size());
        assertEquals(3, subsystem.cameraHelpers[0].getRobotPosesRejected().size());
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
