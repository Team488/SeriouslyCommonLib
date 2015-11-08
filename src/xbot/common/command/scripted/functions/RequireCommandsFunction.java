package xbot.common.command.scripted.functions;
import java.util.Arrays;
import java.util.function.Consumer;

import org.mozilla.javascript.*;

/**
 * A JavaScript-callable function to allow scripts to specify that they require access
 * to certain command types.
 *
 */
public class RequireCommandsFunction extends ScriptedCommandFunctionBase {

    private Consumer<String[]> requiredComandsCallback;
    
    public RequireCommandsFunction(Consumer<String[]> requiredComandsCallback) {
        this.requiredComandsCallback = requiredComandsCallback;
    }

    @Override
    public Object call(Context arg0, Scriptable arg1, Scriptable arg2, Object[] functionParams) {
        // TODO: DO this manually to make sure no errors
        String[] functionParamsAsStrings = Arrays.copyOf(functionParams, functionParams.length, String[].class);
        
        this.requiredComandsCallback.accept(functionParamsAsStrings);
        
        return null;
    }
}
