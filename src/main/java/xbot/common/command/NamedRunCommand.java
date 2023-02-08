package xbot.common.command;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class NamedRunCommand extends RunCommand {
    protected Logger log;

    public NamedRunCommand(String name, Runnable toRun, Subsystem... requirements) {
        super(toRun, requirements);
        this.setName(name);
        log = LogManager.getLogger(this.getName());
    }

    @Override
    public void initialize() {
        super.initialize();
        log.info("Initializing");
    }
}
