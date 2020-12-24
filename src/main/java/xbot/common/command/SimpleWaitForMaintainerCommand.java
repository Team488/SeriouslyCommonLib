package xbot.common.command;

import java.util.function.Supplier;

import xbot.common.controls.sensors.XTimer;

public class SimpleWaitForMaintainerCommand extends BaseCommand {

    private final BaseSetpointSubsystem subsystem;
    private double startTime;
    private Supplier<Double> delaySupplier;

    public SimpleWaitForMaintainerCommand(BaseSetpointSubsystem subsystem, Supplier<Double> delaySupplier) {
        this.subsystem = subsystem;
        this.delaySupplier = delaySupplier;
    }

    @Override
    public void initialize() {
        this.startTime = XTimer.getFPGATimestamp();
    }

    @Override
    public void execute() {
        // Do nothing
    }

    @Override
    public boolean isFinished() {
        return isTimeoutExpired() || subsystem.isMaintainerAtGoal();
    }

    private boolean isTimeoutExpired() {
        return XTimer.getFPGATimestamp() > startTime + delaySupplier.get();
    }
}