package xbot.common.subsystems.vision;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.geometry.Transform3d;

/**
 * Mock AprilTagVisionIO implementation.
 */
public class MockAprilTagVisionIO implements AprilTagVisionIO {

    private final String name;
    private final Transform3d robotToCamera;

    @AssistedFactory
    public abstract static class FactoryImpl implements AprilTagVisionIOFactory {
        public abstract MockAprilTagVisionIO create(String name, Transform3d robotToCamera);
    }

    @AssistedInject
    public MockAprilTagVisionIO(@Assisted String name, @Assisted Transform3d robotToCamera) {
        this.name = name;
        this.robotToCamera = robotToCamera;
    }
}
