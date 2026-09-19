package xbot.common.injection.modules;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

import org.wpilib.fields.Fields;

@Module
public class DefaultVisionModule {
    @Provides
    @Singleton
    static Fields getAprilTagFieldLayout() {
        return Fields.DEFAULT_FIELD;
    }
}
