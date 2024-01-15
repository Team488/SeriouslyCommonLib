package xbot.common.subsystems.drive;

import xbot.common.command.BaseCommand;
import xbot.common.math.XYPair;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.drive.control_logic.HeadingModule.HeadingModuleFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.trajectory.SwerveSimpleTrajectoryLogic;

import javax.inject.Inject;

public class SwerveSimpleTrajectoryCommand extends BaseCommand {

    BaseDriveSubsystem drive;
    BasePoseSubsystem pose;
    HeadingModule headingModule;
    public SwerveSimpleTrajectoryLogic logic;

    @Inject
    public SwerveSimpleTrajectoryCommand(BaseDriveSubsystem drive, BasePoseSubsystem pose, PropertyFactory pf, HeadingModuleFactory headingModuleFactory) {
        this.drive = drive;
        this.pose = pose;
        headingModule = headingModuleFactory.create(drive.getRotateToHeadingPid());

        pf.setPrefix(this);
        this.addRequirements(drive);
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        logic.reset(pose.getCurrentPose2d());
    }

    @Override
    public void execute() {
        var powers = logic.calculatePowers(pose.getCurrentPose2d(), drive.getPositionalPid(), headingModule);

        drive.fieldOrientedDrive(
                new XYPair(powers.dx, powers.dy),
                powers.dtheta, pose.getCurrentHeading().getDegrees(), false);
    }

    @Override
    public boolean isFinished() {
        return logic.recommendIsFinished(pose.getCurrentPose2d(), drive.getPositionalPid(), headingModule);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        if (interrupted) {
            log.warn("Command interrupted");
        }
        drive.stop();
    }
}