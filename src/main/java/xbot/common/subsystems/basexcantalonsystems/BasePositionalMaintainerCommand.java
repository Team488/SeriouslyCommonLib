package xbot.common.subsystems.basexcantalonsystems;

import xbot.common.command.BaseCommand;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.properties.PropertyFactory;

public abstract class BasePositionalMaintainerCommand extends BaseCommand {
    
    public enum MaintainerMode {
        Calibrating, GaveUp, Calibrated
    }

    final BaseXCANTalonPositionControlledSubsystem subsystem;
    final HumanVsMachineDecider decider;
    protected double calibrationEndTime;

    public BasePositionalMaintainerCommand(
        BaseXCANTalonPositionControlledSubsystem subsystem, 
        PropertyFactory propFactory,
        CommonLibFactory clf,
        String prefix) {
        this.subsystem = subsystem;

        requires(subsystem);
        propFactory.setPrefix(prefix);
        decider = clf.createHumanVsMachineDecider(prefix);
    }

    public abstract double getCalibrationAttemptDuration();

    public abstract double getHumanPowerInput();

    @Override
    public void initialize() {
        log.info("Initializing with " + subsystem.getTargetInDomainUnits() + " as a target");
        decider.reset();

        if (!subsystem.getIsCalibrated()) {
            log.info("Subsystem" + subsystem.getName() + " is not calibrated. Calibration phase will begin." );
            setCalibrationEndTime();
        }
        else {
            log.info(subsystem.getName() + " is already calibrated. Setting current position as target position.");
            subsystem.setCurrentPositionAsTargetPosition();
        }
    }

    protected void setCalibrationEndTime() {
        calibrationEndTime = XTimer.getFPGATimestamp() + getCalibrationAttemptDuration();
    }

    @Override
    public void execute() {
        MaintainerMode mode = MaintainerMode.Calibrating;

        // Decide what meta-level activity the subsystem is involved in
        // If the subsystem is uncalibrated, it will try to calibrate.
        // If this takes too long, it gives up, and we are in 100% human control
        // Otherwise, we are in the traditional maintainer mode.
        if (subsystem.getIsCalibrated()) {
            mode = MaintainerMode.Calibrated;
        } else if (XTimer.getFPGATimestamp() < calibrationEndTime) {
            mode = MaintainerMode.Calibrating;
        } else {
            mode = MaintainerMode.GaveUp;
        }

        HumanVsMachineMode deciderMode = decider.getRecommendedMode(getHumanPowerInput());
        double power = 0;

        if (mode == MaintainerMode.Calibrated) {
            switch (deciderMode) {
            case HumanControl:
                power = getHumanPowerInput();
                break;
            case Coast:
                power = 0;
                break;
            case InitializeMachineControl:
                power = 0;
                subsystem.setCurrentPositionAsTargetPosition();
                break;
            case MachineControl:
                double positionOutput = elevator.getPositionalPid().calculate(elevator.getTargetHeight(), elevator.getCurrentHeightInInches());
                double powerDelta = elevator.getVelocityPid().calculate(positionOutput * velocityPIDMaxPower.get(), elevator.getVelocityInchesPerSecond());
                throttle += powerDelta;
                throttle = MathUtils.constrainDouble(throttle, -.8, 1);
                power = throttle;

                break;
            default: 
                power = 0;
                break;
            }
            subsystem.setPower(power);
        } else if (currentMode == MaintainerMode.Calibrating) {
            elevator.lower();
        } else if (currentMode == MaintainerMode.GaveUp) {
            subsystem.setPower(getHumanPowerInput());
        }

    }
}