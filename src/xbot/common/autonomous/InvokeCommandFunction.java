package xbot.common.autonomous;

import java.util.function.Consumer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import edu.wpi.first.wpilibj.command.Command;

public class InvokeCommandFunction extends ScriptedCommandFunctionBase {

    private ScriptedCommandProvider wrappedCommandType;
    private Consumer<Command> notifyInvokedCommand;
    
    public InvokeCommandFunction(ScriptedCommandProvider commandToInvoke, Consumer<Command> notifyInvokedCommand) {
        this.wrappedCommandType = commandToInvoke;
        this.notifyInvokedCommand = notifyInvokedCommand;
    }

    @Override
    public Object call(Context arg0, Scriptable arg1, Scriptable arg2, Object[] arg3) {
        try {
            Command newCommand = wrappedCommandType.get();
            //TODO: Configure command according to parameters
            this.notifyInvokedCommand.accept(newCommand);
        }
        catch (Exception e) {
            // TODO
            e.printStackTrace();
        }
        
        return null;
    }
    
}
