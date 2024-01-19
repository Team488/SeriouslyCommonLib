package xbot.common.injection.swerve;

import xbot.common.subsystems.drive.swerve.SwerveDriveSubsystem;
import xbot.common.subsystems.drive.swerve.SwerveModuleSubsystem;
import xbot.common.subsystems.drive.swerve.SwerveSteeringSubsystem;
import dagger.BindsInstance;
import dagger.Subcomponent;
import xbot.common.subsystems.drive.swerve.commands.SwerveDriveMaintainerCommand;
import xbot.common.subsystems.drive.swerve.commands.SwerveSteeringMaintainerCommand;

/**
 * Subcomponent for a handling dependency injection related to a single swerve drive module.
 */
@SwerveSingleton
@Subcomponent(modules = SwerveModule.class)
public abstract class SwerveComponent {
    public abstract SwerveInstance swerveInstance();

    public abstract SwerveModuleSubsystem swerveModuleSubsystem();

    public abstract SwerveDriveSubsystem swerveDriveSubsystem();

    public abstract SwerveDriveMaintainerCommand swerveDriveMaintainerCommand();

    public abstract SwerveSteeringSubsystem swerveSteeringSubsystem();

    public abstract SwerveSteeringMaintainerCommand swerveSteeringMaintainerCommand();

    @Subcomponent.Builder
    public interface Builder {
        @BindsInstance
        Builder swerveInstance(SwerveInstance instance);

        SwerveComponent build();
    }
}