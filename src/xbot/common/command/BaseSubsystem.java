package xbot.common.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class BaseSubsystem extends Subsystem {

    public BaseSubsystem() { }
    
    public BaseSubsystem(String name) {
        super(name);
    }
    
    public void setDefaultCommand(Command command) {
        super.setDefaultCommand(command);
    }

    @Override
    protected void initDefaultCommand() {

    }

}
