package xbot.common.command.scripted;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import xbot.common.command.BaseCommand;

/**
 * An object which can be exposed to a running script to wrap a
 * command on the scheduler.
 *
 */
public class CommandScriptInterface {
    static Logger log = Logger.getLogger(CommandScriptInterface.class);
    
    private BaseCommand wrappedCommand;
    private ScriptedCommand parentCommand;

    public CommandScriptInterface(BaseCommand wrappedCommand, ScriptedCommand parentCommand) {
        this.wrappedCommand = wrappedCommand;
        this.parentCommand = parentCommand;
        
        log.info("Command script interface for " + wrappedCommand + " created");
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
        return wrappedCommand.isFinished();
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
            long startTime = System.currentTimeMillis();
            synchronized (lock) {
                log.debug("Waiting with a timeout of " + timeoutMillis + "ms");
                lock.wait(timeoutMillis);
            }
            
            log.debug("Wait completed (waited " + (System.currentTimeMillis() - startTime) + "millis)");
        }
        catch (InterruptedException e) {
            log.error("Scripted command wait unexpectedly interrupted!"); 
        }
    }
    
    public void waitForCompletion() {
        waitForCompletion(0);
    }
}
