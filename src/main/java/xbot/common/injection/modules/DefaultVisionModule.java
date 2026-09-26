package xbot.common.injection.modules;

import javax.inject.Singleton;

import org.wpilib.fields.Fields;

import dagger.Module;
import dagger.Provides;

@Module
public class DefaultVisionModule {
    @Provides
    @Singleton
    static Fields getAprilTagFieldLayout() {
        return Fields.DEFAULT_FIELD;
    }
}
