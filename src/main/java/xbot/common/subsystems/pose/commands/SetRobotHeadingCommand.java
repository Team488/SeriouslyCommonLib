package xbot.common.subsystems.pose.commands;

import javax.inject.Inject;

import xbot.common.command.BaseCommand;
import xbot.common.subsystems.pose.BasePoseSubsystem;

import java.util.function.Supplier;

public class SetRobotHeadingCommand extends BaseCommand {

    protected final BasePoseSubsystem poseSubsystem;
    private Supplier<Double> headingSupplier;
    
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

    public void setHeadingToApply(Supplier<Double> headingSupplier) {
        this.headingSupplier = headingSupplier;
    }

    @Override
    public void initialize() {
        log.info("Initializing");
        log.info("Setting heading to " + headingSupplier.get());
        poseSubsystem.setCurrentHeading(headingSupplier.get());
    }

    @Override
    public void execute() {}
    
    @Override
    public boolean isFinished() {
        return true;
    }
}
