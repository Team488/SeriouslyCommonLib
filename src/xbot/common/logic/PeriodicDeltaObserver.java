package xbot.common.logic;

import org.apache.log4j.Logger;

public class PeriodicDeltaObserver {

    protected static Logger log = Logger.getLogger(PeriodicDeltaObserver.class);
    
    private double checkValueThreshold;
    
    private Double lastReportedValue = null;
    
    public PeriodicDeltaObserver(double checkValueThreshold) {
        this.checkValueThreshold = checkValueThreshold;
    }
    
    public void setCheckPowerThreshold(double value) {
        this.checkValueThreshold = value;
    }
    
    public boolean updateAndCheckHasDeltaExceededThreshold(double newReportedValue) {
        boolean significantChange = Math.abs(lastReportedValue - newReportedValue) > checkValueThreshold;
        if (lastReportedValue == null || significantChange) {
            lastReportedValue = newReportedValue;
            return true;
        }
        return false;
    }   
}
