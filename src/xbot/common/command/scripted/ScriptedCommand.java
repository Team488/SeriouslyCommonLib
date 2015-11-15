package xbot.common.command.scripted;

import java.io.File;
import java.lang.Thread.State;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.Set;

import org.apache.derby.iapi.sql.execute.ExecutionFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import xbot.common.command.BaseCommand;
import xbot.common.command.XScheduler;

/**
 * A command to facilitate execution of a JavaScript script with access
 * to robot functionality such as Command invocation.
 *
 */
public class ScriptedCommand extends BaseCommand {

    static Logger log = Logger.getLogger(ScriptedCommand.class);
    
    private XScheduler scheduler;
    
    private ScriptedCommandThread execThread;

    ScriptedCommandFactory availableCommandFactory;
    
    File scriptFile = null;
    String manualScriptText = null, manualScriptName = null;
    
    /**
     * Contains all the commands that this script has invoked.
     * NOTE: Not all commands in this collection are guaranteed to be running;
     * being in this list only signifies that a command was invoked at some
     * point by this script.
     */
    private volatile Set<BaseCommand> invokedCommands;
    
    private volatile Set<ScriptedCommandCompletionLock> completionLocks;
    
    @AssistedInject
    public ScriptedCommand(
            XScheduler scheduler,
            @Assisted File scriptFile,
            @Assisted ScriptedCommandFactory availableCommandFactory) {
        
        this.scheduler = scheduler;
        this.availableCommandFactory = availableCommandFactory;
        
        this.scriptFile = scriptFile;
        
        if(scriptFile == null) {
            log.warn("Instantiated with null file!");
        }
        else if (!scriptFile.canRead() || !scriptFile.isFile()) {
            log.warn("Instantiated with file that can't be read! "
                    + "It either doesn't exist or the robot program doesn't have permission to access it.");
        }
        else {
            log.info("Instantiated with file path\"" + scriptFile.getAbsolutePath() + "\"");
        }
    }
    
    @AssistedInject
    public ScriptedCommand(
            XScheduler scheduler,
            @Assisted("manualScriptText") String manualScriptText,
            @Assisted("manualScriptName") String manualScriptName,
            @Assisted ScriptedCommandFactory availableCommandFactory) {
        
        this.scheduler = scheduler;
        this.availableCommandFactory = availableCommandFactory;
        
        this.manualScriptText = manualScriptText;
        this.manualScriptName = manualScriptName;
        
        if(manualScriptText == null || manualScriptName == null) {
            log.warn("Instantiated with null script or script name!");
        }
        else {
            log.info("Instantiated with script string (" + manualScriptName + " is " + manualScriptText.length() + " chars)");
        }
    }
    
    private void initializeExecThread() {
        log.info("Initializing exec thread");
        
        if(manualScriptText != null && manualScriptName != null) {
            log.info("Using manual script text for exec");
            execThread = new ScriptedCommandThread(manualScriptText, manualScriptName, this, availableCommandFactory);
        }
        else if (scriptFile != null && scriptFile.canRead() && scriptFile.isFile()) {
            log.info("Using file path for exec");
            execThread = new ScriptedCommandThread(scriptFile, this, availableCommandFactory);
        }
        else {
            log.error("Exec thread not initialized due to lack of viable script source.");
            return;
        }
        
        execThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Logger log = Logger.getLogger(ScriptedCommandThread.class);
                log.error("Uncaught exception in autonomous thread! " + e.toString());
                e.printStackTrace();
            }
        });
    }
    
    /**
     * For internal use only!
     * @param command
     */
    public synchronized void invokeCommand(BaseCommand command) {
        log.info("Adding command " + command + " to scheduler");
        
        this.scheduler.add(command);
        if(this.invokedCommands == null)
            this.invokedCommands = new HashSet<BaseCommand>();
        
        this.invokedCommands.add(command);
    }
    
    @Override
    public void initialize() {
        log.info("Initialized");
        initializeExecThread();
    }

    @Override
    public void execute() {
        // Start the execution thread if this is the first command run
        if(execThread.getState() == State.NEW) {
            log.info("Starting exec thread");
            this.execThread.start();
        }
        
        // Update any locks waiting for commands to finish
        if(this.completionLocks != null) {
            Set<ScriptedCommandCompletionLock> locksToRemove = new HashSet<>();
            
            for(ScriptedCommandCompletionLock lock : this.completionLocks) {
                if(lock.updateLock()) {
                    log.debug("Found lock to remove: " + lock.toString());
                    locksToRemove.add(lock);
                }
            }
            
            if(!locksToRemove.isEmpty()) {
                log.info("Removing " + locksToRemove.size() + " locks");
                completionLocks.removeAll(locksToRemove);
            }
        }
    }

    @Override
    public boolean isFinished() {
        log.debug("isFinished called; Thread state is " + (execThread == null ? "[uninitialized]" : execThread.getState().toString()));
        
        boolean isFinished = execThread != null && execThread.getState() == State.TERMINATED;
        log.debug("isFinished is " + isFinished);
        
        return isFinished;
    }

    @Override
    public void end() {
        log.info("Ending");
        if(execThread != null) {
            log.debug("Interrupting exec thread");
            this.execThread.interrupt();
        }
        
        log.debug("Cancelling invoked commands");
        if(this.invokedCommands != null) {
            for(Command command : invokedCommands) {
                log.info("Cancelling command " + command);

                if(!command.isRunning())
                    log.warn("Canceling a command (" + command + ") that isn't running - if it has not started yet, we won't be able to stop it!");
                
                command.cancel();
            }
            
            this.invokedCommands = null;
        }
        else {
            log.info("No invoked commands to cancel");
        }
    }

    @Override
    public void interrupted() {
        log.info("Interrupted");
        end();
    }
    
    public boolean hasReachedCheckpoint(String checkpointName) {
        return execThread != null && execThread.hasReachedCheckpoint(checkpointName);
    }
    
    public void addCompletionLock(ScriptedCommandCompletionLock lock) {
        if(this.completionLocks == null)
            this.completionLocks = new HashSet<>();
        
        this.completionLocks.add(lock);
    }
}
