package xbot.common.autonomous;
import java.util.Arrays;
import java.util.function.Consumer;

import org.mozilla.javascript.*;

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
