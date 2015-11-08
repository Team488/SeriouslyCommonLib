package xbot.common.autonomous;

import java.io.File;
import java.lang.Thread.State;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

/**
 * A command to facilitate execution of a JavaScript script with access
 * to robot functionality such as Command invocation.
 *
 */
public class AutonomousScriptedCommand extends Command {

    static Logger log = Logger.getLogger(AutonomousScriptedCommand.class);
    
    private AutonomousScriptedCommandThread execThread;

    ScriptedCommandFactory availableCommandFactory;
    
    File scriptFile = null;
    String manualScriptText = null, manualScriptName = null;
    
    /**
     * Contains all the commands that this script has invoked.
     * NOTE: Not all commands in this collection are guaranteed to be running;
     * being in this list only signifies that a command was invoked at some
     * point by this script.
     */
    private Set<Command> invokedCommands;
    
    public AutonomousScriptedCommand(File scriptFile, ScriptedCommandFactory availableCommandFactory) {
        this.availableCommandFactory = availableCommandFactory;
        
        this.scriptFile = scriptFile;
    }
    
    public AutonomousScriptedCommand(String manualScriptText, String manualScriptName, ScriptedCommandFactory availableCommandFactory) {
        this.availableCommandFactory = availableCommandFactory;
        
        this.manualScriptText = manualScriptText;
        this.manualScriptName = manualScriptName;
    }
    
    private void initializeExecThread() {
        if(scriptFile == null) {
            execThread = new AutonomousScriptedCommandThread(manualScriptText, manualScriptName, this, availableCommandFactory);
        }
        else {
            execThread = new AutonomousScriptedCommandThread(scriptFile, this, availableCommandFactory);
        }
        
        execThread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                Logger log = Logger.getLogger(AutonomousScriptedCommandThread.class);
                log.error("Uncaught exception in autonomous thread! " + e.toString());
            }
        });
    }
    
    /**
     * For internal use only!
     * @param command
     */
    public synchronized void invokeCommand(Command command) {
        Scheduler.getInstance().add(command);
        if(this.invokedCommands == null)
            this.invokedCommands = new HashSet<Command>();
        
        this.invokedCommands.add(command);
    }
    
    @Override
    protected void initialize() {
        initializeExecThread();
    }

    @Override
    protected void execute() {
        if(execThread.getState() == State.NEW)
            this.execThread.start();
    }

    @Override
    protected boolean isFinished() {
        return execThread != null && execThread.getState() == State.TERMINATED;
    }

    @Override
    protected void end() {
        if(execThread != null) {
            this.execThread.interrupt();
        }
        
        if(this.invokedCommands != null) {
            for(Command command : invokedCommands) {
                command.cancel();
            }
            
            this.invokedCommands = null;
        }
    }

    @Override
    protected void interrupted() {
        end();
    }
    
    public boolean hasReachedCheckpoint(String checkpointName) {
        return execThread != null && execThread.hasReachedCheckpoint(checkpointName);
    }
}
