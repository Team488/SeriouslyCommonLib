package xbot.common.logging;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import edu.wpi.first.wpilibj.DriverStation;

public class DriverStationAppender extends AppenderSkeleton {

    boolean stationExists;

    public DriverStationAppender() {
        try {
            Class.forName("edu.wpi.first.wpilibj.DriverStation", false, this.getClass().getClassLoader());
            stationExists = true;
        } catch (Exception e) {
            // do nothing
        }
    }

    @Override
    public void close() {
        
    }

    @Override
    public boolean requiresLayout() {
        return false;
    }

    @Override
    protected void append(LoggingEvent arg0) {
        if (stationExists) {
            DriverStation.reportError((String) arg0.getMessage(), true);
        }
    }

}
