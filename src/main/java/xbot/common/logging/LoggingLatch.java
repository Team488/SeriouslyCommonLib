package xbot.common.logging;

import org.apache.log4j.Logger;

import xbot.common.logic.Latch;
import xbot.common.logic.Latch.EdgeType;

public class LoggingLatch {

    static Logger log = Logger.getLogger(LoggingLatch.class);
    
    private final Latch latch;
    
    public LoggingLatch(String callerName, String message, EdgeType edgeType) {
        this.latch = new Latch(false, edgeType);
        
        latch.addObserver((e) -> {
            EdgeType edge =e;
            if(edge == edgeType) {
                log.info(callerName + ": " + message);
            }
        });
    }
    
    public void checkValue(boolean value) {
        latch.setValue(value);
    }
}
