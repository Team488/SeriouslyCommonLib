package xbot.common.command;

import java.util.function.DoubleSupplier;

import org.wpilib.system.Timer;

/**
 * Command that waits for a maintainer to be at its goal or until a timeout.
 */
public class SimpleWaitForMaintainerCommand extends BaseCommand {

    private final BaseSetpointSubsystem<?, ?> subsystem;
    private final DoubleSupplier delaySupplier;
    private double startTime;

    /**
     * Creates a new SimpleWaitForMaintainerCommand instance.
     *
     * @param subsystem
     *                      The maintainer subsystem.
     * @param delaySupplier
     *                      The timeout duration in seconds via a supplier.
     */
    public SimpleWaitForMaintainerCommand(BaseSetpointSubsystem<?, ?> subsystem, DoubleSupplier delaySupplier) {
        this.subsystem = subsystem;
        this.delaySupplier = delaySupplier;
    }

    @Override
    public void initialize() {
        this.startTime = Timer.getTimestamp();
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
        return Timer.getTimestamp() > startTime + delaySupplier.getAsDouble();
    }
}
