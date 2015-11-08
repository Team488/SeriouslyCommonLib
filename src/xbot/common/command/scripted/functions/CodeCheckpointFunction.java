package xbot.common.command.scripted.functions;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.mozilla.javascript.*;

/**
 * A JavaScript-callable function to register a script checkpoint.
 *
 */
public class CodeCheckpointFunction extends ScriptedCommandFunctionBase {
    static Logger log = Logger.getLogger(CodeCheckpointFunction.class);

    private Consumer<String> codeCheckpointCallback;
    
    public CodeCheckpointFunction(Consumer<String> codeCheckpointCallback) {
        this.codeCheckpointCallback = codeCheckpointCallback;
    }

    @Override
    public Object call(Context arg0, Scriptable arg1, Scriptable arg2, Object[] functionParams) {
        if(functionParams.length <= 0) {
            log.warn("Autonomous code checkpoint method called with no arguments! Ignoring.");
            return null;
        }
        if(!(functionParams[0] instanceof String)) {
            log.warn("Autonomous code checkpoint method called without a string argument! Ignoring.");
            return null;
        }
        
        this.codeCheckpointCallback.accept((String)functionParams[0]);
        
        return null;
    }
}
