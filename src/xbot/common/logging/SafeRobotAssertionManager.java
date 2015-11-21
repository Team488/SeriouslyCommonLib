package xbot.common.logging;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;

import com.google.inject.Singleton;

@Singleton
public class SafeRobotAssertionManager {
    private boolean allowExceptions = false;
    static Logger log = Logger.getLogger(SafeRobotAssertionManager.class);
    
    public void throwException(RuntimeException e) {
        log.error("Safe exception encountered (exception throw " + (allowExceptions ? "enabled" : "disabled") + "): "
                +  e.getMessage());
        log.error("Stack trace: \n    "
                + Arrays.stream(e.getStackTrace())
                    .map(elem -> elem.toString())
                    .collect(Collectors.joining("\n    ")));
        
        if(allowExceptions) {
            throw e;
        }
    }
    
    public void assertTrue(boolean value, String assertionFaliureCause) {
        if(!value) {
            throwException(new SafeRobotAssertionException(assertionFaliureCause));
        }
    }
    
    public void setExceptionsEnabled(boolean allowException) {
        this.allowExceptions = allowException;
    }
}
