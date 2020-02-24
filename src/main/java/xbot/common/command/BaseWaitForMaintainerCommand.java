package xbot.common.command;

import xbot.common.controls.sensors.XTimer;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

/**
 * Command that waits for a setpoint subsystem to reach its goal
 */
public abstract class BaseWaitForMaintainerCommand extends BaseCommand {

    private final BaseSetpointSubsystem system;
    private final DoubleProperty timeoutProperty;

    private double startTime;

    public BaseWaitForMaintainerCommand(BaseSetpointSubsystem system, PropertyFactory pf, double defaultTimeout) {
        this.system = system;

        pf.setPrefix(this);
        this.timeoutProperty = pf.createPersistentProperty("Timeout Seconds", defaultTimeout);
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
        return XTimer.getFPGATimestamp() > this.startTime + this.timeoutProperty.get();
    }
}