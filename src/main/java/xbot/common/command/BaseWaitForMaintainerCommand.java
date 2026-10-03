package xbot.common.command;

import org.wpilib.tunable.TunableDouble;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.TunableFactory;

/**
 * Command that waits for a setpoint subsystem to reach its goal
 */
public abstract class BaseWaitForMaintainerCommand extends BaseCommand {

    private final BaseSetpointSubsystem<?, ?> system;
    private final TunableDouble timeoutTunable;

    private double startTime;

    public BaseWaitForMaintainerCommand(
            BaseSetpointSubsystem<?, ?> system,
            TunableFactory tunableFactory,
            double defaultTimeout) {
        this.system = system;

        tunableFactory.setPrefix(this);
        this.timeoutTunable = tunableFactory.createDouble("Timeout Seconds", defaultTimeout);
    }

    @Override
    public void initialize() {
        this.startTime = XTimer.getFPGATimestamp();
    }

    @Override
    public void execute() {
        // this command doesn't do anything in the execute phase
    }

    @Override
    public boolean isFinished() {
        return (this.system.isMaintainerAtGoal() || isTimeoutExpired());
    }

    private boolean isTimeoutExpired() {
        return XTimer.getFPGATimestamp() > this.startTime + this.timeoutTunable.get();
    }
}
