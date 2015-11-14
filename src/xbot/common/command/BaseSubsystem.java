package xbot.common.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

public class BaseSubsystem extends Subsystem {

    public void setDefaultCommand(Command command) {
        super.setDefaultCommand(command);
    }

    @Override
    protected void initDefaultCommand() {
        // TODO Auto-generated method stub

    }

}
