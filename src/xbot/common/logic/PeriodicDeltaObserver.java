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
    
    public boolean updateAndCheckHasDeltaExceededThreshold(double reportedValue) {
        if (lastReportedValue == null || Math.abs(lastReportedValue - reportedValue) > checkValueThreshold) {
            lastReportedValue = reportedValue;
            return true;
        }
        return false;
    }
    
}
