package xbot.common.injection.modules;

import dagger.Module;
import dagger.Provides;
import org.wpilib.apriltag.AprilTagFieldLayout;
import org.wpilib.apriltag.AprilTagFields;

import javax.inject.Singleton;

@Module
public class DefaultVisionModule {
    @Provides
    @Singleton
    static AprilTagFieldLayout getAprilTagFieldLayout() {
        return AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
    }
}
