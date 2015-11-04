package xbot.common.autonomous;

import edu.wpi.first.wpilibj.command.Command;

public interface ScriptedCommandProvider {
    public Command get();
}
