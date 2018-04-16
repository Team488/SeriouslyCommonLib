package xbot.common.command;

import com.google.inject.Provider;

import edu.wpi.first.wpilibj.command.Command;

public class RunCommandAfterDelayCommand extends BaseCommandGroup {
    public RunCommandAfterDelayCommand(Command command, double timeout, Provider<TimeoutCommand> timeoutProvider) {
        TimeoutCommand timeoutCommand = timeoutProvider.get();
        timeoutCommand.setConfigurableTimeout(timeout);
        
        this.addSequential(timeoutCommand);
        this.addSequential(command);
        
        this.setName("Delayed " + command.getName() + "(" + timeout + " seconds)" );
    }
}
