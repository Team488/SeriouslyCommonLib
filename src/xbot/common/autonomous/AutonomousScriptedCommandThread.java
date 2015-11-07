package xbot.common.autonomous;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class AutonomousScriptedCommandThread extends Thread {
    static Logger log = Logger.getLogger(AutonomousScriptedCommandThread.class);
    
    Context jsContext;
    Scriptable jsScope;
    
    ScriptableObject robotInterfaceObject;
    
    ScriptedCommandFactory availableCommandFactory;
    AutonomousScriptedCommand parentCommand;
    
    public AutonomousScriptedCommandThread(AutonomousScriptedCommand parentCommand, ScriptedCommandFactory availableCommandFactory) {
        this.parentCommand = parentCommand;
        this.availableCommandFactory = availableCommandFactory;
    }
    
    @Override
    public void run() {
        
    }
    
    public synchronized void initializeScriptEngine() {
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
    
    // TODO: Figure out if we need to call this
    private void uninitializeScope() {
        Context.exit();
        this.jsContext = null;
        this.jsScope = null;
    }
    
    public synchronized void executeScriptFromString(String scriptText, String scriptName) {
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
            
            InvokeCommandFunction commandInvoker = new InvokeCommandFunction(commandProvider, command -> this.parentCommand.invokeCommand(command));
            
            jsScope.put("invoke" + commandTypeName, robotInterfaceObject, commandInvoker);
        }
    }
}
