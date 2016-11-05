package xbot.common.logging;

import org.apache.log4j.Logger;

public class LoggingLatch {

    static Logger log = Logger.getLogger(LoggingLatch.class);
    private String callerName;
    private String message;
    private boolean oldValue;
    
    public LoggingLatch(String callerName, String message) {
        this.callerName = callerName;
        oldValue = false;
    }
    
    public void checkValue(boolean value) {
        if (value && !oldValue) {
            // rising edge!
            log.error("From " + callerName + ": " + message);
        }
        
        oldValue = value;
    }
}
