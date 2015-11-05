package xbot.common.command;

import java.util.Arrays;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class XScheduler {
    
    private static Logger log = Logger.getLogger(XScheduler.class);
    
    boolean crashedPreviously = false;
    
    int numberOfCrashes = 0;
    
    Scheduler scheduler;
    
    public XScheduler() {
        this.scheduler = Scheduler.getInstance();
    }
    
    public int getNumberOfCrashes()
    {
        return numberOfCrashes;
    }
    
    public void run() {
        try {
            scheduler.run();
            crashedPreviously = false;
        } catch(Throwable t) {
            log.error(String.format(
                    "Unhandled exception in Scheduler %s at %s",
                    t.toString(),
                    Arrays.toString(t.getStackTrace())));
            if(crashedPreviously) {
                log.error("Due to repeated exceptions, clearing Scheduler queue completely");
                scheduler.removeAll();
            }
            crashedPreviously = true;
            numberOfCrashes++;
        }
    }
    
    public void add(Command command) {
        scheduler.add(command);
    }
}
