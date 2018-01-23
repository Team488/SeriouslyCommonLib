package xbot.common.logging;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/**
 * Base class for safe assertion manager. Allows context-based management of
 * exceptions and assertion conditions.
 *
 */
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
    
    public final void throwException(String message, Throwable cause) {
        throwException(new RuntimeException(message, cause));
    }

    protected abstract void handlePlatformException(RuntimeException e);
    
    public abstract boolean isExceptionsEnabled();
    
    public final void fail(String message) {
        throwException(new RobotAssertionException(message));
    }
    
    public final void assertTrue(boolean value, String assertionFaliureCause) {
        if(!value) {
            fail(assertionFaliureCause);
        }
    }
}
