package xbot.common.logic;

import java.util.Observable;

public class DeltaObserver extends Observable {

    private double deltaThreshold;
    private Double lastReportedValue = null;
    
    public DeltaObserver(double checkValueThreshold) {
        this.deltaThreshold = checkValueThreshold;
    }
    
    public void setDeltaThreshold(double value) {
        this.deltaThreshold = value;
    }
    
    public void setValue(double value) {
        updateAndCheckHasDeltaExceededThreshold(value);
    }
    
    private void updateAndCheckHasDeltaExceededThreshold(double newReportedValue) {
        boolean significantChange = Math.abs(lastReportedValue - newReportedValue) > deltaThreshold;
        if (lastReportedValue == null || significantChange) {
            lastReportedValue = newReportedValue;
            setChanged();
            notifyObservers(lastReportedValue);
        }
    }   
}
