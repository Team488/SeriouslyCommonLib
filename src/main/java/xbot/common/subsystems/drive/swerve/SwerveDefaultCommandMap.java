package xbot.common.subsystems.drive.swerve;

import xbot.common.injection.swerve.FrontLeftDrive;
import xbot.common.injection.swerve.FrontRightDrive;
import xbot.common.injection.swerve.RearLeftDrive;
import xbot.common.injection.swerve.RearRightDrive;
import xbot.common.injection.swerve.SwerveComponent;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Sets up the default commands for the swerve subsystems
 */
@Singleton
public final class SwerveDefaultCommandMap {
    @Inject
    public SwerveDefaultCommandMap() {}

    @Inject
    public void setupFrontLeftSubsystems(
            @FrontLeftDrive SwerveComponent swerveComponent) {
        swerveComponent.swerveDriveSubsystem().setDefaultCommand(swerveComponent.swerveDriveMaintainerCommand());
        swerveComponent.swerveSteeringSubsystem().setDefaultCommand(swerveComponent.swerveSteeringMaintainerCommand());
    }

    @Inject
    public void setupFrontRightSubsystems(
            @FrontRightDrive SwerveComponent swerveComponent) {
        swerveComponent.swerveDriveSubsystem().setDefaultCommand(swerveComponent.swerveDriveMaintainerCommand());
        swerveComponent.swerveSteeringSubsystem().setDefaultCommand(swerveComponent.swerveSteeringMaintainerCommand());
    }

    @Inject
    public void setupRearLeftSubsystems(
            @RearLeftDrive SwerveComponent swerveComponent) {
        swerveComponent.swerveDriveSubsystem().setDefaultCommand(swerveComponent.swerveDriveMaintainerCommand());
        swerveComponent.swerveSteeringSubsystem().setDefaultCommand(swerveComponent.swerveSteeringMaintainerCommand());
    }

    @Inject
    public void setupRearRightSubsystems(
            @RearRightDrive SwerveComponent swerveComponent) {
        swerveComponent.swerveDriveSubsystem().setDefaultCommand(swerveComponent.swerveDriveMaintainerCommand());
        swerveComponent.swerveSteeringSubsystem().setDefaultCommand(swerveComponent.swerveSteeringMaintainerCommand());
    }
}
