package xbot.common.logic;

import org.apache.log4j.Logger;

public class PeriodicDeltaObserver {

    protected static Logger log = Logger.getLogger(PeriodicDeltaObserver.class);
    
    private double checkValueThreshold;
    
    private double oldValue = 0;
    
    public PeriodicDeltaObserver(double checkValueThreshold) {
        this.checkValueThreshold = checkValueThreshold;
    }
    
    public void setCheckPowerThreshold(double value) {
        this.checkValueThreshold = value;
    }
    
    public boolean isDelta(double newValue) {
        if (Math.abs(oldValue - newValue) > checkValueThreshold) {
            oldValue = newValue;
            return true;
        }
        oldValue = newValue;
        return false;
    }
    
}
