package xbot.common.command;

import edu.wpi.first.wpilibj2.command.Subsystem;

public abstract class BaseSetpointSubsystem extends BaseSubsystem implements SupportsSetpointLock {

    private Subsystem setpointLock;
    
    public BaseSetpointSubsystem() {
        setpointLock = new Subsystem() {};
    }

    @Override
    public Subsystem getSetpointLock() {
       return setpointLock;
    }    
}
