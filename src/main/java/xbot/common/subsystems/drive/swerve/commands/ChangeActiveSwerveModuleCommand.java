package xbot.common.subsystems.drive.swerve.commands;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;

import javax.inject.Inject;

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
