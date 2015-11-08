package xbot.common.command.scripted.functions;

import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import edu.wpi.first.wpilibj.command.Command;
import xbot.common.command.scripted.ScriptedCommandProvider;

/**
 * A JavaScript-callable function to invoke a command.
 *
 */
public class InvokeCommandFunction extends ScriptedCommandFunctionBase {
    static Logger log = Logger.getLogger(CodeCheckpointFunction.class);

    private ScriptedCommandProvider wrappedCommandType;
    private Consumer<Command> notifyInvokedCommand;
    
    public InvokeCommandFunction(ScriptedCommandProvider commandToInvoke, Consumer<Command> notifyInvokedCommand) {
        this.wrappedCommandType = commandToInvoke;
        this.notifyInvokedCommand = notifyInvokedCommand;
    }

    @Override
    public Object call(Context arg0, Scriptable arg1, Scriptable arg2, Object[] parameters) {
        try {
            Command newCommand = wrappedCommandType.get(parameters);
            //TODO: Configure command according to parameters
            this.notifyInvokedCommand.accept(newCommand);
        }
        catch (Exception e) {
            log.error("An error occurred while attempting to invoke a comman in an autonomous script.");
        }
        
        return null;
    }
    
}
