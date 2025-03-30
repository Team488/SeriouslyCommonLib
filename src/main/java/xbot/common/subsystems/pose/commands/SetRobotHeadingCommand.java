package xbot.common.subsystems.pose.commands;

import javax.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.pose.BasePoseSubsystem;

import java.util.function.DoubleSupplier;

public class SetRobotHeadingCommand extends BaseCommand {

    protected final BasePoseSubsystem poseSubsystem;
    private DoubleSupplier headingSupplier;

    @Inject
    public SetRobotHeadingCommand(BasePoseSubsystem poseSubsystem) {
        this.poseSubsystem = poseSubsystem;
        headingSupplier = () -> BasePoseSubsystem.FACING_AWAY_FROM_DRIVERS;
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }

    public void setHeadingToApply(double heading) {
        this.headingSupplier = () -> heading;
    }

    public void setHeadingToApply(DoubleSupplier headingSupplier) {
        this.headingSupplier = headingSupplier;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        log.info("Setting heading to " + headingSupplier.getAsDouble());
        poseSubsystem.setCurrentHeading(headingSupplier.getAsDouble());
    }

    @Override
    public void execute() {}

    @Override
    public boolean isFinished() {
        return true;
    }
}
