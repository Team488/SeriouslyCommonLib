package xbot.common.subsystems.drive.swerve.commands;

import xbot.common.command.BaseMaintainerCommand;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineDeciderFactory;
import xbot.common.properties.PropertyFactory;
import xbot.common.subsystems.drive.swerve.SwerveSteeringSubsystem;

import javax.inject.Inject;

public class SwerveSteeringMaintainerCommand extends BaseMaintainerCommand<Double, Double> {

    private final SwerveSteeringSubsystem subsystem;

    @Inject
    public SwerveSteeringMaintainerCommand(SwerveSteeringSubsystem subsystemToMaintain, PropertyFactory pf, HumanVsMachineDeciderFactory hvmFactory) {
        super(subsystemToMaintain, pf, hvmFactory, 0.001, 0.001);
        pf.setPrefix(this);

        this.subsystem = subsystemToMaintain;
    }

    @Override
    protected void coastAction() {
        this.subsystem.setPower(0.0);
    }

    @Override
    protected void calibratedMachineControlAction() {
        this.subsystem.setMotorControllerPidTarget();
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
        // never hooked directly to human input, human input handled by drive
        return 0.0;
    }

    @Override
    public void initialize() {
        this.subsystem.setTargetValue(this.subsystem.getCurrentValue());
        this.subsystem.setPower(0.0);
    }

    @Override
    public void end(boolean interrupted) {
        super.end(interrupted);
        this.initialize();
    }
}