package xbot.common.command;

import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class NamedRunCommand extends RunCommand {
    public NamedRunCommand(String name, Runnable toRun, Subsystem... requirements) {
        super(toRun, requirements);
        this.setName(name);
    }
}