package xbot.common.logging;

import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;

public class OneLogFilePerRunRollingFileAppender extends RollingFileAppender {

    public OneLogFilePerRunRollingFileAppender() {
        super();
    }

    public OneLogFilePerRunRollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
        super(layout, filename, append);
    }

    public OneLogFilePerRunRollingFileAppender(Layout layout, String filename) throws IOException {
        super(layout, filename);
    }
    
    @Override
    public void activateOptions() {
        super.activateOptions();
        this.rollOverManually();
    }
    
    public synchronized void rollOverManually() {
        this.rollOver();
    }

}
