package xbot.common.injection.modules;

import dagger.Module;
import dagger.Provides;
import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;

import javax.inject.Singleton;

@Module
public class DefaultVisionModule {
    @Provides
    @Singleton
    static AprilTagFieldLayout getAprilTagFieldLayout() {
        return AprilTagFieldLayout.loadField(AprilTagFields.kDefaultField);
    }
}
