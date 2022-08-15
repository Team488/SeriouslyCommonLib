package xbot.common.properties;

import javax.inject.Singleton;

@Singleton
public class DebugFlag {
    boolean isDebug = false;
    
    public void setDebug(boolean debug) {
        isDebug = debug;
    }
    
    public boolean isDebug() {
        return isDebug;
    }
}
