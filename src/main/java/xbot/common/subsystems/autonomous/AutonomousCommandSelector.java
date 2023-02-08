package xbot.common.subsystems.autonomous;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj2.command.Command;

import xbot.common.command.BaseSubsystem;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

@Singleton
public class AutonomousCommandSelector extends BaseSubsystem {
    private static Logger log = Logger.getLogger(AutonomousCommandSelector.class);

    public final StringProperty currentAutonomousCommandName;
    public final StringProperty currentAutonomousState;
    Supplier<Command> commandSupplier;

    Command currentAutonomousCommand;

    @Inject
    public AutonomousCommandSelector(PropertyFactory propFactory) {
        propFactory.setTopLevelPrefix();
        currentAutonomousCommandName = propFactory.createEphemeralProperty("Current autonomous command name",
                "No command set");
        currentAutonomousState = propFactory.createEphemeralProperty("Auto Program State", "Not set");
    }

    public Command getCurrentAutonomousCommand() {
        if (commandSupplier != null) {
            return commandSupplier.get();
        }
        return currentAutonomousCommand;
    }

    public void setCurrentAutonomousCommand(Command currentAutonomousCommand) {
        log.info("Setting CurrentAutonomousCommand to " + currentAutonomousCommand);
        this.currentAutonomousCommandName
                .set(currentAutonomousCommand == null ? "No command set" : currentAutonomousCommand.getName());

        this.currentAutonomousCommand = currentAutonomousCommand;
        commandSupplier = null;
    }
    
    public void setCurrentAutonomousCommandSupplier(Supplier<Command> supplier) {
        commandSupplier = supplier;
        this.currentAutonomousCommand = null;
    }

    public void setAutonomousState(String state) {
        currentAutonomousState.set(state);
    }

}
