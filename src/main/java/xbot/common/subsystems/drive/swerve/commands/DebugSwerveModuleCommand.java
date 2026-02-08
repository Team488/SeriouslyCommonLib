package xbot.common.subsystems.drive.swerve.commands;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.sensors.XXboxController;
import xbot.common.subsystems.drive.OperatorInterface;
import xbot.common.command.BaseCommand;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;

import javax.inject.Inject;

public class DebugSwerveModuleCommand extends BaseCommand {

    final BaseSwerveDriveSubsystem drive;
    final XXboxController gamepad;

    @AssistedFactory
    public abstract static class DebugSwerveModuleCommandFactory {
        public abstract DebugSwerveModuleCommand create(
                @Assisted XXboxController gamepad);
    }

    @AssistedInject
    public DebugSwerveModuleCommand(BaseSwerveDriveSubsystem drive, @Assisted XXboxController gamepad) {
        this.drive = drive;
        this.gamepad = gamepad;
        this.addRequirements(drive);

        this.addRequirements(drive.getFrontLeftSwerveModuleSubsystem().getDriveSubsystem());
        this.addRequirements(drive.getFrontRightSwerveModuleSubsystem().getDriveSubsystem());
        this.addRequirements(drive.getRearLeftSwerveModuleSubsystem().getDriveSubsystem());
        this.addRequirements(drive.getRearRightSwerveModuleSubsystem().getDriveSubsystem());

        this.addRequirements(drive.getFrontLeftSwerveModuleSubsystem().getSteeringSubsystem());
        this.addRequirements(drive.getFrontRightSwerveModuleSubsystem().getSteeringSubsystem());
        this.addRequirements(drive.getRearLeftSwerveModuleSubsystem().getSteeringSubsystem());
        this.addRequirements(drive.getRearRightSwerveModuleSubsystem().getSteeringSubsystem());
    }

    @Override
    public void initialize() {
        log.info("Initializing");
    }

    @Override
    public void execute() {
        double drivePower = gamepad.getLeftStickY();
        double turnPower = gamepad.getRightStickX();

        drive.controlOnlyActiveSwerveModuleSubsystem(drivePower, turnPower);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}