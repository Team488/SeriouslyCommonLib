package xbot.common.injection.modules;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import org.wpilib.vision.apriltag.AprilTagFieldLayout;
import org.wpilib.vision.apriltag.AprilTagFields;

@Module
public class DefaultVisionModule {
    @Provides
    @Singleton
    static AprilTagFieldLayout getAprilTagFieldLayout() {
        return AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
    }
}
