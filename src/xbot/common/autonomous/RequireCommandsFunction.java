package xbot.common.autonomous;
import java.util.function.Consumer;

import org.mozilla.javascript.*;

public class RequireCommandsFunction implements Function {

    private Consumer<String[]> requiredComandsCallback;
    
    public RequireCommandsFunction(Consumer<String[]> requiredComandsCallback) {
        this.requiredComandsCallback = requiredComandsCallback;
    }

    @Override
    public Object call(Context arg0, Scriptable arg1, Scriptable arg2, Object[] functionParams) {
        this.requiredComandsCallback.accept((String[]) functionParams);
        
        return null;
    }
    
    
    @Override
    public void delete(String arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void delete(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object get(String arg0, Scriptable arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object get(int arg0, Scriptable arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getClassName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getDefaultValue(Class<?> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object[] getIds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scriptable getParentScope() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Scriptable getPrototype() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean has(String arg0, Scriptable arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean has(int arg0, Scriptable arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean hasInstance(Scriptable arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void put(String arg0, Scriptable arg1, Object arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void put(int arg0, Scriptable arg1, Object arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setParentScope(Scriptable arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setPrototype(Scriptable arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Scriptable construct(Context arg0, Scriptable arg1, Object[] arg2) {
        // TODO Auto-generated method stub
        return null;
    }

}
