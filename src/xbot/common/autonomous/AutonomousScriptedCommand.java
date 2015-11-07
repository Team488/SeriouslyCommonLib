package xbot.common.autonomous;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class AutonomousScriptedCommand extends Command {

    static Logger log = Logger.getLogger(AutonomousScriptedCommand.class);
    
    private AutonomousScriptedCommandThread execThread;

    ScriptedCommandFactory availableCommandFactory;
    
    /**
     * Contains all the commands that this script has invoked.
     * NOTE: Not all commands in this collection are guaranteed to be running;
     * being in this list only signifies that a command was invoked at some
     * point by this script.
     */
    private Set<Command> invokedCommands;
    
    public AutonomousScriptedCommand(ScriptedCommandFactory availableCommandFactory) {
        this.availableCommandFactory = availableCommandFactory;
        
        initializeScriptEngine();
    }
    
    private void initializeScriptEngine() {
        execThread = new AutonomousScriptedCommandThread(this, availableCommandFactory);
        execThread.initializeScriptEngine();
    }
    
    /**
     * Executes the given script string.
     * WARNING: This should only be used for debugging. Real robots should use
     * the constructor to pass in a target file.
     * 
     * @param scriptText
     * @param scriptName
     */
    public void executeScriptFromString(String scriptText, String scriptName) {
        execThread.executeScriptFromString(scriptText, scriptName);
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
        if(execThread == null)
            initializeScriptEngine();
    }

    @Override
    protected void execute() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean isFinished() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void end() {
        this.execThread.interrupt();
        if(this.invokedCommands != null) {
            for(Command command : invokedCommands) {
                command.cancel();
            }
            
            this.invokedCommands = null;
        }
        this.execThread = null;
    }

    @Override
    protected void interrupted() {
        end();
    }

}
