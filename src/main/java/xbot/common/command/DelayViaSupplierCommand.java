package xbot.common.command;

import java.util.function.Supplier;

import xbot.common.controls.sensors.XTimer;

public class DelayViaSupplierCommand extends BaseCommand {

    protected double startTime;
    protected Supplier<Double> delaySupplier;

    public DelayViaSupplierCommand(Supplier<Double> delaySupplier) {
        this.delaySupplier = delaySupplier;
    }

    @Override
    public void initialize() {
        this.startTime = XTimer.getFPGATimestamp();
        log.info("Initializing with intial time: " + startTime + " and waiting for " + delaySupplier.get() + " seconds");
    }

    @Override
    public void execute() {
        // Do nothing
    }

    @Override
    public boolean isFinished() {
        return isTimeoutExpired();
    }

    private boolean isTimeoutExpired() {
        return XTimer.getFPGATimestamp() > startTime + delaySupplier.get();
    }
}
