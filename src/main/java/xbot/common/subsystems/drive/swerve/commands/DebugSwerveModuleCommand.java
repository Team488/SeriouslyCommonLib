package competition.subsystems.drive.commands;

import competition.operator_interface.OperatorInterface;
import competition.subsystems.drive.DriveSubsystem;
import edu.wpi.first.math.MathUtil;
import xbot.common.command.BaseCommand;

import javax.inject.Inject;

public class DebugSwerveModuleCommand extends BaseCommand {

    final DriveSubsystem drive;
    final OperatorInterface oi;

    @Inject
    public DebugSwerveModuleCommand(DriveSubsystem drive, OperatorInterface oi) {
        this.drive = drive;
        this.oi = oi;
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
        double drivePower = MathUtil.applyDeadband(oi.driverGamepad.getLeftStickY(), oi.getDriverGamepadTypicalDeadband());
        double turnPower = MathUtil.applyDeadband(oi.driverGamepad.getRightStickX(), oi.getDriverGamepadTypicalDeadband());

        drive.controlOnlyActiveSwerveModuleSubsystem(drivePower, turnPower);
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}