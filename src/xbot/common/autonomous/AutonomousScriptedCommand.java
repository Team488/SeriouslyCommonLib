package xbot.common.autonomous;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import xbot.common.command.BaseRobot;

public class AutonomousScriptedCommand extends Command {

    static Logger log = Logger.getLogger(AutonomousScriptedCommand.class);
    
    Context jsContext;
    Scriptable jsScope;
    
    ScriptableObject robotInterfaceObject;
    
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
        // TODO: Figure out if this will work with multiple simultaneous scripted commands
        jsContext = Context.enter();
        jsScope = jsContext.initStandardObjects();
        robotInterfaceObject = new ScriptableObject() {
            
            @Override
            public String getClassName() {
                return "RobotInterface";
            }
        };
        jsScope.put("robot",  jsScope, robotInterfaceObject);
        robotInterfaceObject.put("requireCommands", robotInterfaceObject,
                new RequireCommandsFunction(commandNames -> this.requireCommands(commandNames)));
        
    }
    
    public void executeScriptFromString(String scriptText, String scriptName) {
        this.jsContext.evaluateString(jsScope, scriptText, scriptName, 1, null);
    }
    
    private void requireCommands(String[] commandNames) {
        if(this.jsContext == null) {
            log.error("An attempt was made to require a command after the JS context had been"
                    + "de-initialized. Ignoring this attempt.");
            return;
        }
        
        for(String commandTypeName : commandNames) {
            log.info("Autonomous script required command " + commandTypeName);
            
            ScriptedCommandProvider commandProvider = this.availableCommandFactory.getProviderForName(commandTypeName);
            
            if(commandProvider == null) {
                log.error("Unable to get provider for command type " + commandTypeName + "!");
                continue;
            }
            
            InvokeCommandFunction commandInvoker = new InvokeCommandFunction(commandProvider, command -> this.invokeCommand(command));
            
            jsScope.put("invoke" + commandTypeName, robotInterfaceObject, commandInvoker);
        }
    }
    
    private void invokeCommand(Command command) {
        Scheduler.getInstance().add(command);
        if(this.invokedCommands == null)
            this.invokedCommands = new HashSet<Command>();
        
        this.invokedCommands.add(command);
    }
    
    @Override
    protected void initialize() {
        // If we have already run and been interrupted once, we need to re-init
        //  the context.
        if(jsContext == null)
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
        if(this.invokedCommands != null) {
            for(Command command : invokedCommands) {
                command.cancel();
            }
            
            this.invokedCommands = null;
        }
        
        Context.exit();
        this.jsContext = null;
        this.jsScope = null;
    }

    @Override
    protected void interrupted() {
        end();
    }

}
