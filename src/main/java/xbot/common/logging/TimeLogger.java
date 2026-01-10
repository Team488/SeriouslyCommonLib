package xbot.common.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import xbot.common.controls.sensors.XTimer;

public class TimeLogger {

    private static Logger log = LogManager.getLogger(TimeLogger.class);

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
        lastReportTime = getPerformanceTimestamp();
    }

    public void start() {
        startTime = getPerformanceTimestamp();

        if (firstCall) {
            firstCall = false;
            reset();
        }
    }

    public void stop() {
        double duration = getPerformanceTimestamp() - startTime;

        accumulatedTime += duration;
        callCount++;

        if (getPerformanceTimestamp() - lastReportTime > reportingIntervalInSeconds && callCount > 0) {
            double averageLoopDuration = accumulatedTime / (double)callCount;
            log.info("Average duration for " + name + " was: " + averageLoopDuration);

            reset();
        }
    }

    private double getPerformanceTimestamp() {
        return XTimer.getFPGATimestamp() / 1000000.0;
    }
}
