package xbot.common.command;

import java.util.function.Supplier;

import xbot.common.controls.sensors.XTimer;

/**
 * Command that waits for a maintainer to be at its goal or until a timeout.
 */
public class SimpleWaitForMaintainerCommand extends BaseCommand {

    private final BaseSetpointSubsystem<?> subsystem;
    private final Supplier<Double> delaySupplier;
    private double startTime;

    /**
     * Creates a new SimpleWaitForMaintainerCommand instance.
     * @param subsystem The maintainer subsystem.
     * @param delaySupplier The timeout duration in seconds via a supplier.
     */
    public SimpleWaitForMaintainerCommand(BaseSetpointSubsystem<?> subsystem, Supplier<Double> delaySupplier) {
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