package xbot.common.command;

import edu.wpi.first.wpilibj2.command.Subsystem;
import xbot.common.controls.sensors.XTimer;

/**
 * Base class for subsystems that have a setpoint managed by a maintainer.
 * @param <T> The type of the target value.
 */
public abstract class BaseSetpointSubsystem<T> extends BaseSubsystem implements SupportsSetpointLock {

    private final Subsystem setpointLock;
    private double lastUpdateTimeFromMaintainer;
    protected boolean atGoal;
    
    public BaseSetpointSubsystem() {
        setpointLock = new Subsystem() {};
    }

    @Override
    public Subsystem getSetpointLock() {
       return setpointLock;
    }
    
    public boolean isMaintainerAtGoal() {
        // If we haven't heard from the maintainer within the last 0.5 seconds,
        // then assume the system is not ready.
        double gap = XTimer.getFPGATimestamp() - lastUpdateTimeFromMaintainer;
        return atGoal && Math.abs(gap) < 0.5;
    }

    public void setMaintainerIsAtGoal(boolean atGoal) {
        lastUpdateTimeFromMaintainer = XTimer.getFPGATimestamp();
        this.atGoal = atGoal;
    }

    public abstract T getCurrentValue();

    public abstract T getTargetValue();

    public abstract void setTargetValue(T value);

    public abstract void setPower(T power);

    public abstract boolean isCalibrated();
}
