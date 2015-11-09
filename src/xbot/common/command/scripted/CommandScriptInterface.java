package xbot.common.command.scripted;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import xbot.common.command.BaseCommand;

public class CommandScriptInterface {
    static Logger log = Logger.getLogger(CommandScriptInterface.class);
    
    private Command wrappedCommand;
    private ScriptedCommand parentCommand;

    public CommandScriptInterface(Command wrappedCommand, ScriptedCommand parentCommand) {
        this.wrappedCommand = wrappedCommand;
        this.parentCommand = parentCommand;
    }
    
    public boolean canWaitForCompletion() {
        return isBaseCommand();
    }
    
    public boolean canCheckIsFinished() {
        return isBaseCommand();
    }
    
    public boolean isBaseCommand() {
        return this.wrappedCommand instanceof BaseCommand;
    }
    
    public boolean isRunning() {
        return wrappedCommand.isRunning();
    }

    public boolean isFinished() {
        if(!(this.wrappedCommand instanceof BaseCommand)) {
            log.error("isFinished called on command which doesn't inherit from BaseCommand!"
                    + " Unable to check if command has finished.");
            
            return false;
        }
        
        return ((BaseCommand)wrappedCommand).isFinished();
    }
    
    public void waitForCompletion(long timeoutMillis) {
        if(!(this.wrappedCommand instanceof BaseCommand)) {
            log.error("waitForCompletion called on command which doesn't inherit from BaseCommand!"
                    + " Unable to wait for command to complete.");
            
            return;
        }
        
        ScriptedCommandCompletionLock lock = new ScriptedCommandCompletionLock((BaseCommand)wrappedCommand);
        parentCommand.addCompletionLock(lock);

        try {
            synchronized (lock) {
                if(timeoutMillis <= 0) {
                    lock.wait();
                }
                else {
                    lock.wait(timeoutMillis);
                }
            }
        }
        catch (InterruptedException e) {
            log.error("Scripted command wait unexpectedly interrupted!"); 
        }
    }
    
    public void waitForCompletion() {
        waitForCompletion(0);
    }
}
