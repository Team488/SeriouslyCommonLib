package xbot.common.command;

import edu.wpi.first.wpilibj2.command.Subsystem;
import xbot.common.controls.sensors.XTimer;

public abstract class BaseSetpointSubsystem extends BaseSubsystem implements SupportsSetpointLock {

    private Subsystem setpointLock;
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
}
