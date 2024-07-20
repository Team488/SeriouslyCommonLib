package xbot.common.subsystems.autonomous;

import java.util.Objects;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import edu.wpi.first.wpilibj2.command.InstantCommand;
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
    boolean isDefault;

    @Inject
    public AutonomousCommandSelector(PropertyFactory propFactory) {
        propFactory.setTopLevelPrefix();
        setAutonomousState("Not set");
        isDefault = true;
    }

    public Command getCurrentAutonomousCommand() {
        if (commandSupplier != null) {
            return commandSupplier.get();
        }
        return currentAutonomousCommand;
    }

    public void setCurrentAutonomousCommand(Command currentAutonomousCommand) {
        log.info("Setting CurrentAutonomousCommand to " + currentAutonomousCommand);
        aKitLog.record("Current autonomous command name",
                currentAutonomousCommand == null ? "No command set" : currentAutonomousCommand.getName());

        this.currentAutonomousCommand = currentAutonomousCommand;
        commandSupplier = null;
        isDefault = false;
    }
    
    public void setCurrentAutonomousCommandSupplier(Supplier<Command> supplier) {
        commandSupplier = supplier;
        this.currentAutonomousCommand = null;
    }

    public void setAutonomousState(String state) {
        aKitLog.record("Auto Program State", state);
    }

    public Command createAutonomousStateMessageCommand(String message) {
        return new  InstantCommand(() -> {
                    this.setAutonomousState(message);
        });
    }

    public void setIsDefault(boolean bol) {
        this.isDefault = bol;
    }

    public boolean getIsDefault() {
        return this.isDefault;
    }

    public String getProgramName() {
        return getCurrentAutonomousCommand() == null ? "" : getCurrentAutonomousCommand().getName();
    }
}
