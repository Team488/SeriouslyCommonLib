package xbot.common.logging;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XTimer;

public class TimeLogger {
    
    private static Logger log = Logger.getLogger(TimeLogger.class);

    boolean firstCall;
    double lastReportTime;
    double startTime;
    String name;
    double accumulatedTime;
    int callCount;
    int reportingIntervalInSeconds; 
    XTimer timer;
    
    public TimeLogger(String name, int reportingIntervalInSeconds, XTimer timer) {
        firstCall = true;
        this.name = name;
        this.timer = timer;
        this.reportingIntervalInSeconds = reportingIntervalInSeconds;
        reset();
    }
    
    private void reset() {
        accumulatedTime = 0;
        callCount = 0;
        lastReportTime = timer.getFPGATimestamp();
    }
    
    public void start() {
        startTime = timer.getFPGATimestamp();
        
        if (firstCall) {
            firstCall = false;
            reset();
        }
    }
    
    public void stop() {
        double duration = timer.getFPGATimestamp() - startTime;
        
        accumulatedTime += duration;
        callCount++;
        
        if (timer.getFPGATimestamp() - lastReportTime > reportingIntervalInSeconds && callCount > 0) {
            double averageLoopDuration = accumulatedTime / (double)callCount;
            log.info("Average duration for " + name + " was: " + averageLoopDuration);
            
            reset();
        }
    }
}
