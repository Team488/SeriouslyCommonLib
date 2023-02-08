package xbot.common.command;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Subsystem;

/**
 * Wrapper for base Scheduler which intelligently manages exceptions.
 */
@Singleton
public class XScheduler {
    
    private static Logger log = Logger.getLogger(XScheduler.class);
    
    boolean crashedPreviously = false;
    
    int numberOfCrashes = 0;
    
    CommandScheduler scheduler;
    
    @Inject
    public XScheduler() {
        this.scheduler = CommandScheduler.getInstance();
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
                scheduler.cancelAll();
            }
            crashedPreviously = true;
            numberOfCrashes++;
        }
    }

    public void removeAll() {
        scheduler.cancelAll();
    }

    public void registerSubsystem(Subsystem... subsystems) {
        scheduler.registerSubsystem(subsystems);
    }

    public void setDefaultCommand(Subsystem subsystem, Command defaultCommand) {
        scheduler.setDefaultCommand(subsystem, defaultCommand);
    }
}
