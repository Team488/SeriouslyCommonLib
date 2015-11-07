package xbot.common.autonomous;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
    
    File scriptFile;
    String manualScriptText, manualScriptName;
    
    public AutonomousScriptedCommandThread(
            File scriptFile,
            AutonomousScriptedCommand parentCommand,
            ScriptedCommandFactory availableCommandFactory) {
        
        this.scriptFile = scriptFile;
        this.parentCommand = parentCommand;
        this.availableCommandFactory = availableCommandFactory;
    }
    
    public AutonomousScriptedCommandThread(
            String scriptText,
            String scriptName,
            AutonomousScriptedCommand parentCommand,
            ScriptedCommandFactory availableCommandFactory) {
        
        this.manualScriptText = scriptText;
        this.manualScriptName = scriptName;
        this.parentCommand = parentCommand;
        this.availableCommandFactory = availableCommandFactory;
    }
    
    @Override
    public void run() {
        if(this.scriptFile == null) {
            this.jsContext.evaluateString(jsScope, manualScriptText, manualScriptName, 1, null);
        }
        else {
            FileReader reader;
            try {
                reader = new FileReader(this.scriptFile);
                this.jsContext.evaluateReader(jsScope, reader, this.scriptFile.getName(), 1, null);
            } catch (FileNotFoundException e) {
                log.error("The given script file could not be found. Execution of this script will not continue. File (\"" + this.scriptFile.getPath() + "\")");
            }
            catch (IOException e) {
                log.error("An error was encountered while reading from the script file (\"" + this.scriptFile.getPath() + "\")");
            }
        }
        
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
