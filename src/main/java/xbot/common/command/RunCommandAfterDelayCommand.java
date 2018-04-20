package xbot.common.command;

import com.google.inject.Provider;

import edu.wpi.first.wpilibj.command.Command;

public class RunCommandAfterDelayCommand extends BaseCommandGroup {
    public RunCommandAfterDelayCommand(Command command, double delay, double commandTimeout, Provider<TimeoutCommand> timeoutProvider) {
        TimeoutCommand timeoutCommand = timeoutProvider.get();
        timeoutCommand.setConfigurableTimeout(delay);
        
        this.addSequential(timeoutCommand);
        if (commandTimeout > 0) {
        	this.addSequential(command, commandTimeout);
        }
        else {
        	this.addSequential(command);
        }
        
        this.setName("Delayed " + command.getName() + "(" + delay + " seconds)" );
    }
    
    public RunCommandAfterDelayCommand(Command command, double delay, Provider<TimeoutCommand> timeoutProvider) {
        this (command, delay, -1, timeoutProvider);
    }
}
