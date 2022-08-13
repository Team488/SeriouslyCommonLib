package xbot.common.injection.modules;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import xbot.common.subsystems.drive.BaseDriveSubsystem;
import xbot.common.subsystems.drive.MockDriveSubsystem;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.subsystems.pose.MockBasePoseSubsystem;

@Module
public abstract class UnitTestRobotModule {
    @Binds
    @Singleton
    abstract BasePoseSubsystem getBasePoseSubsystem(MockBasePoseSubsystem impl);

    @Binds
    @Singleton
    abstract BaseDriveSubsystem getBaseDriveSubsystem(MockDriveSubsystem impl);
}
