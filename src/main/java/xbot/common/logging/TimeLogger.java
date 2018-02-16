package xbot.common.logging;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.Timer;

public class TimeLogger {
    
    private static Logger log = Logger.getLogger(TimeLogger.class);

    boolean firstCall;
    double lastReportTime;
    double startTime;
    String name;
    double accumulatedTime;
    int callCount;
    int reportingIntervalInSeconds; 
    
    public TimeLogger(String name, int reportingIntervalInSeconds) {
        firstCall = true;
        this.name = name;
        this.reportingIntervalInSeconds = reportingIntervalInSeconds;
        reset();
    }
    
    private void reset() {
        accumulatedTime = 0;
        callCount = 0;
        lastReportTime = Timer.getFPGATimestamp();
    }
    
    public void start() {
        startTime = Timer.getFPGATimestamp();
        
        if (firstCall) {
            firstCall = false;
            reset();
        }
    }
    
    public void stop() {
        double duration = Timer.getFPGATimestamp() - startTime;
        
        accumulatedTime += duration;
        callCount++;
        
        if (Timer.getFPGATimestamp() - lastReportTime > reportingIntervalInSeconds && callCount > 0) {
            double averageLoopDuration = accumulatedTime / (double)callCount;
            log.info("Average duration for " + name + " was: " + averageLoopDuration);
            
            reset();
        }
    }
}
