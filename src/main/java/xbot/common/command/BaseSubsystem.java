package xbot.common.command;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public abstract class BaseSubsystem extends Subsystem {
    
    protected Logger log;

    public BaseSubsystem() {
        log = Logger.getLogger(this.getName());
    }

    public BaseSubsystem(String name) {
        super(name);
        log = Logger.getLogger(this.getName());
    }
    
    public void setDefaultCommand(Command command) {
        super.setDefaultCommand(command);
    }

    @Override
    protected void initDefaultCommand() {

    }

}
