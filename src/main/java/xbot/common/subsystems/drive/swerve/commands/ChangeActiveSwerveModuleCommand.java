package xbot.common.subsystems.drive.swerve.commands;

import javax.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;

public class ChangeActiveSwerveModuleCommand extends BaseCommand {

    final BaseSwerveDriveSubsystem drive;

    @Inject
    public ChangeActiveSwerveModuleCommand(BaseSwerveDriveSubsystem drive) {
        this.drive = drive;
    }

    @Override
    public void initialize() {
        drive.setNextModuleAsActiveModule();
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
