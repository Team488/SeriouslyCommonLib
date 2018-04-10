package xbot.common.logging;

import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.RollingFileAppender;

public class OneLogFilePerRunRollingFileAppender extends RollingFileAppender {

    public OneLogFilePerRunRollingFileAppender() {
        super();
        this.rollOver();
    }

    public OneLogFilePerRunRollingFileAppender(Layout layout, String filename, boolean append) throws IOException {
        super(layout, filename, append);
        this.rollOver();
    }

    public OneLogFilePerRunRollingFileAppender(Layout layout, String filename) throws IOException {
        super(layout, filename);
        this.rollOver();
    }

}
