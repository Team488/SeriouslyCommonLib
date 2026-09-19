package xbot.common.command;

import org.wpilib.command2.InstantCommand;
import org.wpilib.command2.Subsystem;

/**
 * Convenience wrapper of InstantCommand that allows setting a human readable name for
 * smartdashboard/logging readability.
 */
public class NamedInstantCommand extends InstantCommand {
    public NamedInstantCommand(String name, Runnable toRun, Subsystem... requirements) {
        super(toRun, requirements);
        this.setName(name);
    }
}