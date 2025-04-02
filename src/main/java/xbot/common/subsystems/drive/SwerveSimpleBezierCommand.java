package xbot.common.subsystems.drive;

import edu.wpi.first.math.geometry.Twist2d;
import xbot.common.command.BaseCommand;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.XYPair;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.control_logic.HeadingModule;
import xbot.common.subsystems.drive.control_logic.HeadingModule.HeadingModuleFactory;
import xbot.common.subsystems.pose.BasePoseSubsystem;
import xbot.common.trajectory.SwerveSimpleBezierLogic;
import xbot.common.trajectory.SwerveSimpleTrajectoryLogic;

import javax.inject.Inject;
import java.util.function.Supplier;

public class SwerveSimpleBezierCommand extends BaseCommand {

    protected BaseSwerveDriveSubsystem drive;
    protected BasePoseSubsystem pose;
    protected HeadingModule headingModule;
    public SwerveSimpleBezierLogic logic;
    public Supplier<Double> constantRotationPowerSupplier;
    protected Supplier<Boolean> alternativeIsFinishedSupplier;
    public boolean constantRotationEnabled = false;

    @Inject
    public SwerveSimpleBezierCommand(BaseSwerveDriveSubsystem drive, BasePoseSubsystem pose, PropertyFactory pf,
                                     HeadingModuleFactory headingModuleFactory, RobotAssertionManager assertionManager) {
        this.drive = drive;
        this.pose = pose;
        headingModule = headingModuleFactory.create(drive.getRotateToHeadingPid());

        pf.setPrefix(this);
        this.addRequirements(drive);
        logic = new SwerveSimpleBezierLogic(assertionManager);
        alternativeIsFinishedSupplier = () -> false;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        reset();
    }

    public void reset() {
        logic.reset(pose.getCurrentPose2d());
    }

    public void setConstantRotationPowerSupplier(Supplier<Double> constantRotationPowerSupplier) {
        this.constantRotationEnabled = true;
        this.constantRotationPowerSupplier = constantRotationPowerSupplier;
    }

    @Override
    public void execute() {
        Twist2d powers = logic.calculatePowers(pose.getCurrentPose2d(), drive.getPositionalPid(),
                headingModule, drive.getMaxTargetSpeedMetersPerSecond());

        if (constantRotationEnabled) {
            if (constantRotationPowerSupplier != null) {
                powers.dtheta = constantRotationPowerSupplier.get();
            }
        }

        aKitLog.record("Powers", powers);

        drive.fieldOrientedDrive(
                new XYPair(powers.dx, powers.dy),
                powers.dtheta, pose.getCurrentHeading().getDegrees(), false);
    }

    @Override
    public boolean isFinished() {
        return logic.recommendIsFinished(pose.getCurrentPose2d(), drive.getPositionalPid(), headingModule)
                || alternativeIsFinishedSupplier.get();
    }

    public void setAlternativeIsFinishedSupplier(Supplier<Boolean> alternativeIsFinishedSupplier) {
        this.alternativeIsFinishedSupplier = alternativeIsFinishedSupplier;
    }

    public Supplier<Boolean> getAlternativeIsFinishedSupplier() {
        return alternativeIsFinishedSupplier;
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
