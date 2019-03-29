package xbot.common.subsystems.basexcantalonsystems;

import xbot.common.command.BaseCommand;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logic.HumanVsMachineDecider;
import xbot.common.logic.HumanVsMachineDecider.HumanVsMachineMode;
import xbot.common.math.MathUtils;
import xbot.common.properties.PropertyFactory;

public abstract class BaseElevatorMaintainerCommand extends BaseCommand {
    
    public enum MaintainerMode {
        Calibrating, GaveUp, Calibrated
    }

    final BaseElevatorSubsystem elevator;
    final HumanVsMachineDecider decider;
    protected double calibrationEndTime;
    double throttle;

    public BaseElevatorMaintainerCommand(
        BaseElevatorSubsystem elevator, 
        PropertyFactory propFactory,
        CommonLibFactory clf,
        String prefix) {
        this.elevator = elevator;

        requires(elevator);
        propFactory.setPrefix(prefix);
        decider = clf.createHumanVsMachineDecider(prefix);
    }

    public abstract double getCalibrationAttemptDuration();
    public abstract double getHumanPowerInput();
    public abstract double getMaximumVelocityInDomainUnits();
    public abstract double getMaximumOutputPower();
    public abstract double getMinimumOutputPower();
    public abstract double getTimeToMaxPower();

    @Override
    public void initialize() {
        log.info("Initializing with " + elevator.getTargetInDomainUnits() + " as a target");
        decider.reset();

        if (!elevator.getIsCalibrated()) {
            log.info("Subsystem" + elevator.getName() + " is not calibrated. Calibration phase will begin." );
            setCalibrationEndTime();
        }
        else {
            log.info(elevator.getName() + " is already calibrated. Setting current position as target position.");
            elevator.setCurrentPositionAsTargetPosition();
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
        if (elevator.getIsCalibrated()) {
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
                elevator.setCurrentPositionAsTargetPosition();
                break;
            case MachineControl:
                double positionOutput = 
                    elevator.getPositionalPidManager().calculate(
                        elevator.getTargetInDomainUnits(), 
                        elevator.getCurrentPositionInDomainUnits());

                double powerDelta = 
                    elevator.getVelocityPidManager().calculate(
                        positionOutput * getMaximumVelocityInDomainUnits(),
                        elevator.getCurrentVelocityInDomainUnits());
                
                double powerDeltaTimeAdjusted = 1 / (50 * getTimeToMaxPower());

                throttle += powerDeltaTimeAdjusted;
                throttle = MathUtils.constrainDouble(throttle, getMinimumOutputPower(), getMaximumOutputPower());
                power = throttle;
                break;
            default: 
                power = 0;
                break;
            }
            elevator.setPower(power);
        } else if (mode == MaintainerMode.Calibrating) {
            elevator.setPower(-elevator.getUncalibratedPower());
        } else if (mode == MaintainerMode.GaveUp) {
            elevator.setPower(getHumanPowerInput());
        }

    }
}