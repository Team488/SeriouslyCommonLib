package xbot.common.autonomous;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import edu.wpi.first.wpilibj.command.Command;

public class AutonomousScriptedCommand extends Command {
    Context jsContext;
    Scriptable jsScope;
    
    ScriptableObject robotInterfaceObject;
    
    ScriptedCommandFactory availableCommandFactory;
    
    public AutonomousScriptedCommand(ScriptedCommandFactory availableCommandFactory) {
        this.availableCommandFactory = availableCommandFactory;
        
        // TODO: Make this universal within the app and make sure to exit
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
    
    public void executeScriptFromString(String scriptText) {
        // TODO: Replace with better input system
        this.jsContext.evaluateString(jsScope, scriptText, "Test script", 1, null);
    }
    
    private void requireCommands(String[] commandNames) {
        
        for(String commandTypeName : commandNames) {
            System.out.println("Required " + commandTypeName);
            ScriptedCommandProvider commandProvider = this.availableCommandFactory.getProviderForName(commandTypeName);
            
            // TODO: Handle nulls
            
            InvokeCommandFunction commandInvoker = new InvokeCommandFunction(commandProvider, command -> this.invokeCommand(command));
            
            jsScope.put("invoke" + commandTypeName, robotInterfaceObject, commandInvoker);
            System.out.println("Done");
        }
    }
    
    private void invokeCommand(Command command) {
        System.out.println("Invoked!!!");
        // TODO
        // TODO: Set parent on new command
    }
    
    @Override
    protected void initialize() {
        // TODO Auto-generated method stub

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
        // TODO Auto-generated method stub

    }

    @Override
    protected void interrupted() {
        // TODO Auto-generated method stub

    }

}
