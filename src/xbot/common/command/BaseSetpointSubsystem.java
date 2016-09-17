package xbot.common.command;

import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class BaseSetpointSubsystem extends BaseSubsystem implements SupportsSetpointLock {

    private Subsystem setpointLock;
    
    public BaseSetpointSubsystem() {
        setpointLock = new Subsystem() {
            @Override
            protected void initDefaultCommand() {
                // Do nothing
            }
        };
    }

    @Override
    public Subsystem getSetpointLock() {
       return setpointLock;
    }    
}
