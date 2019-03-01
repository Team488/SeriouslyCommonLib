package xbot.common.subsystems.basexcantalonsystems;

import xbot.common.command.BaseCommand;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.logic.HumanVsMachineDecider;
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
            subsystem.setTargetInDomainUnits(subsystem.getCurrentPositionInDomainUnits());
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
    }
}