package xbot.common.logic;

import org.apache.log4j.Logger;

public class PeriodicDeltaObserver {

    protected static Logger log = Logger.getLogger(PeriodicDeltaObserver.class);
    
    private double checkPowerThreshold;
    
    private double oldPower = 0;
    
    public PeriodicDeltaObserver(double checkPowerThreshold) {
        this.checkPowerThreshold = checkPowerThreshold;
    }
    
    public void setCheckPowerThreshold(double power) {
        this.checkPowerThreshold = power;
    }
    
    public boolean isDelta(double newPower) {
        if (Math.abs(oldPower - newPower) > checkPowerThreshold) {
            oldPower = newPower;
            return true;
        }
        oldPower = newPower;
        return false;
    }
    
}
