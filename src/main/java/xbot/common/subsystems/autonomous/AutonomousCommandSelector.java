package xbot.common.subsystems.autonomous;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.wpilib.math.geometry.Pose2d;
import org.wpilib.tunable.Tunable;
import org.wpilib.tunable.TunableConfig;
import org.wpilib.tunable.TunableOption;
import org.wpilib.tunable.Tunables;
import org.wpilib.command2.InstantCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.wpilib.command2.Command;

import xbot.common.command.BaseSubsystem;
import xbot.common.properties.TunableFactory;

@Singleton
public class AutonomousCommandSelector extends BaseSubsystem {
    private static Logger log = LogManager.getLogger(AutonomousCommandSelector.class);
    Supplier<Command> commandSupplier;

    Command currentAutonomousCommand;
    Pose2d currentAutonomousStartingPosition;
    boolean isDefault;
    private final Tunable<String> currentAutonomousCommandName;

    @Inject
    public AutonomousCommandSelector(TunableFactory tunableFactory) {
        tunableFactory.setTopLevelPrefix();
        currentAutonomousCommandName = Tunable.createConfig(
                "No command set",
                TunableConfig.of(TunableOption.IMMUTABLE));
        Tunables.publish("Current autunomous command name", currentAutonomousCommandName);
        setAutonomousState("Not set");
        isDefault = true;
    }

    public Command getCurrentAutonomousCommand() {
        if (commandSupplier != null) {
            return commandSupplier.get();
        }
        return currentAutonomousCommand;
    }

    public Pose2d getCurrentAutonomousStartingPosition(){
        return currentAutonomousStartingPosition;
    }

    public void setCurrentAutonomousStartingPosition(Pose2d position){
        this.currentAutonomousStartingPosition = position;
    }

    public void setCurrentAutonomousCommand(Command currentAutonomousCommand) {
        log.info("Setting CurrentAutonomousCommand to " + currentAutonomousCommand);
        log.info("Setting CurrentStartingPosition to " + currentAutonomousStartingPosition);
        var nameString = currentAutonomousCommand == null ? "No command set" : currentAutonomousCommand.getName();
        aKitLog.record("Current autonomous command name", nameString);
        
        currentAutonomousCommandName.set(nameString);

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
