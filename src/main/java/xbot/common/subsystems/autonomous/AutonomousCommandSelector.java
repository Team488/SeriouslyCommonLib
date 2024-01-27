package xbot.common.subsystems.autonomous;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.wpi.first.wpilibj2.command.Command;

import xbot.common.command.BaseSubsystem;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.StringProperty;

@Singleton
public class AutonomousCommandSelector extends BaseSubsystem {
    private static Logger log = LogManager.getLogger(AutonomousCommandSelector.class);
    Supplier<Command> commandSupplier;

    Command currentAutonomousCommand;

    @Inject
    public AutonomousCommandSelector(PropertyFactory propFactory) {
        propFactory.setTopLevelPrefix();
        setAutonomousState("Not set");
    }

    public Command getCurrentAutonomousCommand() {
        if (commandSupplier != null) {
            return commandSupplier.get();
        }
        return currentAutonomousCommand;
    }

    public void setCurrentAutonomousCommand(Command currentAutonomousCommand) {
        log.info("Setting CurrentAutonomousCommand to " + currentAutonomousCommand);
        org.littletonrobotics.junction.Logger.recordOutput(
                this.getPrefix() + "Current autonomous command name",
                currentAutonomousCommand == null ? "No command set" : currentAutonomousCommand.getName());

        this.currentAutonomousCommand = currentAutonomousCommand;
        commandSupplier = null;
    }
    
    public void setCurrentAutonomousCommandSupplier(Supplier<Command> supplier) {
        commandSupplier = supplier;
        this.currentAutonomousCommand = null;
    }

    public void setAutonomousState(String state) {
        org.littletonrobotics.junction.Logger.recordOutput(this.getPrefix() + "Auto Program State", state);
    }

}
