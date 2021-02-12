package xbot.common.subsystems.autonomous;

import java.util.function.Supplier;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj2.command.Command;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import xbot.common.command.BaseSubsystem;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

@Singleton
public class AutonomousCommandSelector extends BaseSubsystem {
    private static Logger log = Logger.getLogger(AutonomousCommandSelector.class);

    public final StringProperty currentAutonomousCommandName;
    Supplier<Command> commandSupplier;

    Command currentAutonomousCommand;

    @Inject
    public AutonomousCommandSelector(PropertyFactory propFactory) {
        propFactory.setTopLevelPrefix();
        currentAutonomousCommandName = propFactory.createEphemeralProperty("Current autonomous command name",
                "No command set");
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

}
