package xbot.common.logging;

import org.apache.log4j.Logger;

public class PeriodicDeltaObserver {

    protected static Logger log = Logger.getLogger(PeriodicDeltaObserver.class);
    private final String callerName;
    private String message;
    
    private double checkPowerThreshold;
    
    private double oldPower = 0;
    
    public PeriodicDeltaObserver(String callerName, String message, double checkPowerThreshold) {
        this.callerName = callerName;
        this.message = message;
        this.checkPowerThreshold = checkPowerThreshold;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public void setCheckPowerThreshold(double power) {
        this.checkPowerThreshold = power;
    }
    
    public void checkPower(double newPower) {
        if (Math.abs(oldPower - newPower) > checkPowerThreshold) {
            log.info("From " + callerName + ": " + message);
        }
        oldPower = newPower;
    }
    
}
