package xbot.common.subsystems.vision;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj.Timer;

import java.lang.annotation.Target;

/**
 * Mock AprilTagVisionIO implementation.
 */
public class MockAprilTagVisionIO implements AprilTagVisionIO {

    private final String name;
    private final Transform3d robotToCamera;

    public boolean connected = true;
    public int[] tagIds = {};
    public PoseObservation[] poseObservations = {};
    public TargetObservation latestTargetObservation = new TargetObservation(0, 1, new Rotation2d(), new Rotation2d(),
            new Transform3d(), 1, false);
    public TargetObservation[] targetObservations = { latestTargetObservation };

    @AssistedFactory
    public abstract static class FactoryImpl implements AprilTagVisionIOFactory {
        public abstract MockAprilTagVisionIO create(String name, Transform3d robotToCamera);
    }

    @AssistedInject
    public MockAprilTagVisionIO(@Assisted String name, @Assisted Transform3d robotToCamera) {
        this.name = name;
        this.robotToCamera = robotToCamera;
    }

    @Override
    public void updateInputs(VisionIOInputs inputs) {
        this.latestTargetObservation = new TargetObservation(Timer.getFPGATimestamp(),
                this.latestTargetObservation.fiducialId(), this.latestTargetObservation.tx(),
                this.latestTargetObservation.ty(), this.latestTargetObservation.cameraToTarget(),
                this.latestTargetObservation.ambiguity(), false);
        inputs.connected = this.connected;
        inputs.tagIds = this.tagIds;
        inputs.poseObservations = this.poseObservations;
        inputs.latestTargetObservation = this.latestTargetObservation;
        inputs.targetObservations = this.targetObservations;
    }
}
