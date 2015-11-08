package xbot.common.autonomous.functions;
import java.util.Arrays;
import java.util.function.Consumer;

import org.mozilla.javascript.*;

/**
 * An abstract implementation of the JavaScript Function interface to provide
 * blank defaults for function implementations.
 *
 */
public abstract class ScriptedCommandFunctionBase implements Function {

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
        return this.getClass().getName();
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
