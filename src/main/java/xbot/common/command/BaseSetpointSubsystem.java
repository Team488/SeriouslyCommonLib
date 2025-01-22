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
    protected T lastTargetValueUsedforAtGoal;

    public BaseSetpointSubsystem() {
        setpointLock = new Subsystem() {};
    }

    @Override
    public Subsystem getSetpointLock() {
       return setpointLock;
    }

    public boolean isMaintainerAtGoal() {

        if (lastTargetValueUsedforAtGoal == null) {
            // Nobody has ever called setMaintainerIsAtGoal.
            return false;
        }

        if (!areTwoTargetsEquivalent(getTargetValue(), lastTargetValueUsedforAtGoal)) {
            // At goal was set when the target was significantly different than it is now.
            // Could happen if somebody updates the target and immediately calls isMaintainerAtGoal.
            return false;
        }

        if (XTimer.getFPGATimestamp() - lastUpdateTimeFromMaintainer > 0.5) {
            // If we haven't heard from the maintainer within the last 0.5 seconds,
            // then assume the system is not ready.
            return false;
        }

        return atGoal;
    }

    public void setMaintainerIsAtGoal(boolean atGoal) {
        lastUpdateTimeFromMaintainer = XTimer.getFPGATimestamp();
        this.atGoal = atGoal;
        lastTargetValueUsedforAtGoal = getTargetValue();
    }

    public abstract T getCurrentValue();

    public abstract T getTargetValue();

    public abstract void setTargetValue(T value);

    public abstract void setPower(double power);

    public abstract boolean isCalibrated();

    protected abstract boolean areTwoTargetsEquivalent(T target1, T target2);

    public SetTargetCommand<T> createSetTargetCommand() {
        return new SetTargetCommand<T>(this);
    }

    public SetTargetCommand<T> createSetTargetCommand(T value) {
        var command = new SetTargetCommand<T>(this);
        command.setTargetValue(value);
        return command;
    }

    // Implementation for most common kind of setpoint subsystem
    public static boolean areTwoDoublesEquivalent(double target1, double target2) {
        return Math.abs(target1 - target2) < 0.00001;
    }

    // Implementation for most common kind of setpoint subsystem
    public static boolean areTwoDoublesEquivalent(double target1, double target2, double tolerance) {
        return Math.abs(target1 - target2) < tolerance;
    }

}
