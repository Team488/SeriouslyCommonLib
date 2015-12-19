package xbot.common.logging;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

public abstract class RobotAssertionManager {
    static Logger log = Logger.getLogger(RobotAssertionManager.class);
    
    public final void throwException(RuntimeException e) {
        log.error("Safe exception encountered (exception throw " + (this.isExceptionsEnabled() ? "enabled" : "disabled") + "): "
                +  e.getMessage());
        log.error("Stack trace: \n    "
                + Arrays.stream(e.getStackTrace())
                    .map(elem -> elem.toString())
                    .collect(Collectors.joining("\n    ")));
        
        handlePlatformException(e);
    }
    
    protected abstract void handlePlatformException(RuntimeException e);
    
    public abstract boolean isExceptionsEnabled();
    
    public final void throwException(String message, Throwable cause) {
        throwException(new RuntimeException(message, cause));
    }
    
    public final void assertTrue(boolean value, String assertionFaliureCause) {
        if(!value) {
            throwException(new RobotAssertionException(assertionFaliureCause));
        }
    }
}
