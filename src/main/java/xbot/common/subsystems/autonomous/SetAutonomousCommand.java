package xbot.common.subsystems.autonomous;

import javax.inject.Inject;

import edu.wpi.first.wpilibj2.command.Command;
import xbot.common.command.BaseCommand;

public class SetAutonomousCommand extends BaseCommand {

    private final AutonomousCommandSelector selector;
    private Command autonomousCommand;

    @Inject
    public SetAutonomousCommand(AutonomousCommandSelector selector) {
        this.selector = selector;
    }

    @Override
    public boolean runsWhenDisabled() {
        return true;
    }

    public void setAutoCommand(Command autonomousCommand) {
        this.autonomousCommand = autonomousCommand;
    }

    @Override
    public void initialize() {
        if (autonomousCommand != null) {
            log.info("Setting Auto to: " + autonomousCommand.getName());
            selector.setCurrentAutonomousCommand(autonomousCommand);
        } else {
            log.warn("No autonomous command configured. Not changing the current autonomous command.");
        }
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}