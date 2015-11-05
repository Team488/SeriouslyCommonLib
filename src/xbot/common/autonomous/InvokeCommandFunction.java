package xbot.common.autonomous;

import java.util.function.Consumer;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

import edu.wpi.first.wpilibj.command.Command;

public class InvokeCommandFunction implements Function {

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
    
    @Override
    public void delete(String arg0) {
        // Intentionally left unimplemented (not needed)        
    }

    @Override
    public void delete(int arg0) {
        // Intentionally left unimplemented (not needed)
    }

    @Override
    public Object get(String arg0, Scriptable arg1) {
        // Intentionally left unimplemented (not needed)
        return null;
    }

    @Override
    public Object get(int arg0, Scriptable arg1) {
        // Intentionally left unimplemented (not needed)
        return null;
    }

    @Override
    public String getClassName() {
        // Intentionally left unimplemented (not needed)
        return "";
    }

    @Override
    public Object getDefaultValue(Class<?> arg0) {
        // Intentionally left unimplemented (not needed)
        return null;
    }

    @Override
    public Object[] getIds() {
        // Intentionally left unimplemented (not needed)
        return null;
    }

    @Override
    public Scriptable getParentScope() {
        // Intentionally left unimplemented (not needed)
        return null;
    }

    @Override
    public Scriptable getPrototype() {
        // Intentionally left unimplemented (not needed)
        return null;
    }

    @Override
    public boolean has(String arg0, Scriptable arg1) {
        // Intentionally left unimplemented (not needed)
        return false;
    }

    @Override
    public boolean has(int arg0, Scriptable arg1) {
        // Intentionally left unimplemented (not needed)
        return false;
    }

    @Override
    public boolean hasInstance(Scriptable arg0) {
        // Intentionally left unimplemented (not needed)
        return false;
    }

    @Override
    public void put(String arg0, Scriptable arg1, Object arg2) {
        // Intentionally left unimplemented (not needed)
        
    }

    @Override
    public void put(int arg0, Scriptable arg1, Object arg2) {
        // Intentionally left unimplemented (not needed)
        
    }

    @Override
    public void setParentScope(Scriptable arg0) {
        // Intentionally left unimplemented (not needed)
        
    }

    @Override
    public void setPrototype(Scriptable arg0) {
        // Intentionally left unimplemented (not needed)
        
    }

    @Override
    public Scriptable construct(Context arg0, Scriptable arg1, Object[] arg2) {
        // Intentionally left unimplemented (not needed)
        return null;
    }
    
}
