package xbot.common.command;

import edu.wpi.first.wpilibj2.command.Subsystem;
import xbot.common.controls.sensors.XTimer;

/**
 * Base class for subsystems that have a setpoint managed by a maintainer.
 * @param <TargetT> The type of the target value.
 * @param <PowerT> The type of the power value.
 */
public abstract class BaseSetpointSubsystem<TargetT, PowerT> extends BaseSubsystem implements SupportsSetpointLock {

    private final Subsystem setpointLock;
    private double lastUpdateTimeFromMaintainer;
    protected boolean atGoal;
    protected TargetT lastTargetValueUsedforAtGoal;

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

    public abstract TargetT getCurrentValue();

    public abstract TargetT getTargetValue();

    public abstract void setTargetValue(TargetT value);

    public abstract void setPower(PowerT power);

    public abstract boolean isCalibrated();

    protected abstract boolean areTwoTargetsEquivalent(TargetT target1, TargetT target2);

    public SetTargetCommand<TargetT> createSetTargetCommand() {
        return new SetTargetCommand<>(this);
    }

    public SetTargetCommand<TargetT> createSetTargetCommand(TargetT value) {
        var command = new SetTargetCommand<>(this);
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
