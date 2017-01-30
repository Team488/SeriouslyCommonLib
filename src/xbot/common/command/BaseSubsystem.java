package xbot.common.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BaseSubsystem extends Subsystem {

    public void setDefaultCommand(Command command) {
        super.setDefaultCommand(command);
    }

    @Override
    protected void initDefaultCommand() {

    }
    
    public void updateTelemetry() {
        // Override in subclass if desired
        throw new NotImplementedException();
    }

}
