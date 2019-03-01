package xbot.common.subsystems.basexcantalonsystems;

import xbot.common.command.BaseCommand;
import xbot.common.properties.PropertyFactory;

public abstract class BasePositionalMaintainerCommand extends BaseCommand {

    BaseXCANTalonPositionControlledSubsystem subsystem;

    public BasePositionalMaintainerCommand(BaseXCANTalonPositionControlledSubsystem subsystem, PropertyFactory propFactory,
            String prefix) {
        requires(subsystem);
        propFactory.setPrefix(prefix);
    }

    public abstract double getCalibrationAttemptDuration();

    @Override
    public void initialize() {
        log.info("Initializing with " + subsystem.getTargetInDomainUnits() + " as a target");
    }

    @Override
    public void execute() {

    }
}