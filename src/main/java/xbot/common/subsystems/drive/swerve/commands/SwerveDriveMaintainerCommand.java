package xbot.common.subsystems.drive.swerve.commands;

import xbot.common.command.BaseSimpleMaintainerCommand;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.BaseSwerveDriveSubsystem;
import xbot.common.subsystems.drive.swerve.SwerveDriveSubsystem;

import javax.inject.Inject;

public class SwerveDriveMaintainerCommand extends BaseSimpleMaintainerCommand {

    private final SwerveDriveSubsystem subsystem;
    private final BaseSwerveDriveSubsystem drive;

    @Inject
    public SwerveDriveMaintainerCommand(BaseSwerveDriveSubsystem drive, SwerveDriveSubsystem subsystemToMaintain,
                                        PropertyFactory pf, HumanVsMachineDeciderFactory hvmFactory) {
        super(subsystemToMaintain, pf, hvmFactory, 0.001, 0.001);
        this.subsystem = subsystemToMaintain;
        this.drive = drive;
    }

    @Override
    protected void coastAction() {
        this.subsystem.setPower(0.0);
    }

    @Override
    protected void calibratedMachineControlAction() {

        if (subsystem.getDrivePidEnabled()) {
            // Let the SparkMax do the PID for us.
            this.subsystem.setMotorControllerVelocityPidFromSubsystemTarget();
        } else {
            // The drive subsystem is setting velocity goals, but we're starting simple.
            // Just set % power by dividing by the max allowable velocity.
            if (drive.getMaxTargetSpeedMetersPerSecond() > 0) {
                this.subsystem.setPower(this.subsystem.getTargetValue() / drive.getMaxTargetSpeedMetersPerSecond());
            } else {
                this.subsystem.setPower(0.0);
            }
        }
    }

    @Override
    protected double getErrorMagnitude() {
        return Math.abs((this.subsystem.getTargetValue() - this.subsystem.getCurrentValue()));
    }

    @Override
    protected Double getHumanInput() {
        // never hooked directly to human input, human input handled by drive
        return 0.0;
    }

    @Override
    protected double getHumanInputMagnitude() {
        return getHumanInput();
    }

    @Override
    public void initialize() {
        this.subsystem.setTargetValue(0.0);
        this.subsystem.setPower(0.0);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);

        this.initialize();
    }
}