package xbot.common.autonomous;
import java.util.Arrays;
import java.util.function.Consumer;

import org.apache.log4j.Logger;
import org.mozilla.javascript.*;

public class CodeCheckpointFunction implements Function {
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
    
    
    @Override
    public void delete(String arg0) {
        
    }

    @Override
    public void delete(int arg0) {
        
    }

    @Override
    public Object get(String arg0, Scriptable arg1) {
        return null;
    }

    @Override
    public Object get(int arg0, Scriptable arg1) {
        return null;
    }

    @Override
    public String getClassName() {
        return null;
    }

    @Override
    public Object getDefaultValue(Class<?> arg0) {
        return null;
    }

    @Override
    public Object[] getIds() {
        return null;
    }

    @Override
    public Scriptable getParentScope() {
        return null;
    }

    @Override
    public Scriptable getPrototype() {
        return null;
    }

    @Override
    public boolean has(String arg0, Scriptable arg1) {
        return false;
    }

    @Override
    public boolean has(int arg0, Scriptable arg1) {
        return false;
    }

    @Override
    public boolean hasInstance(Scriptable arg0) {
        return false;
    }

    @Override
    public void put(String arg0, Scriptable arg1, Object arg2) {
        
    }

    @Override
    public void put(int arg0, Scriptable arg1, Object arg2) {
        
    }

    @Override
    public void setParentScope(Scriptable arg0) {
        
    }

    @Override
    public void setPrototype(Scriptable arg0) {
        
    }

    @Override
    public Scriptable construct(Context arg0, Scriptable arg1, Object[] arg2) {
        return null;
    }

}
