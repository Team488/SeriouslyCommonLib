package xbot.common.command;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import xbot.common.injection.RobotModule;
import xbot.common.properties.XPropertyManager;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Core Robot class which configures logging, properties,
 * scheduling, and the injector. Required for a fair amount
 * of CommonLib functionality.
 */
public class BaseRobot extends IterativeRobot {

    static Logger log = Logger.getLogger(BaseRobot.class);

    protected XPropertyManager propertyManager;
    protected XScheduler xScheduler;

    protected AbstractModule injectionModule;

    // Other than initially creating required systems, you should never use the injector again
    protected Injector injector;
    
    protected Command autonomousCommand;
    
    protected ArrayList<PeriodicDataSource> periodicDataSources = new ArrayList<PeriodicDataSource>();

    public BaseRobot() {
        super();
        setupInjectionModule();
    }
    
    /**
     * Override if you need a different module
     */
    protected void setupInjectionModule() {
        this.injectionModule = new RobotModule();
    }

    /**
     * This function is run when the robot is first started up and should be used for any initialization code.
     * 
     * Info on the warning suppression
     * http://help.eclipse.org/indigo/index.jsp?topic=%2Forg.eclipse.jdt.doc.user%2Ftasks%2Ftask-suppress_warnings.htm
     */
    public void robotInit() {

        // Get our logging config
        try {
            DOMConfigurator.configure("log4j.xml");
        } catch (Exception e) {
            // Had a problem loading the config. Robot should continue!
            System.out.println("Couldn't configure logging - file probably missing or malformed");
        }

        this.injector = Guice.createInjector(this.injectionModule);
        this.initializeSystems();
        SmartDashboard.putData(Scheduler.getInstance());
    }

    protected void initializeSystems() {
        // override with additional systems (but call this one too)

        // Get the property manager and get all properties from the robot disk
        propertyManager = this.injector.getInstance(XPropertyManager.class);
        xScheduler = this.injector.getInstance(XScheduler.class);
    }

    @Override
    public void disabledInit() {
        log.info("Disabled init");
        propertyManager.saveOutAllProperties();
    }

    public void disabledPeriodic() {
        this.sharedPeriodic();
    }

    public void autonomousInit() {
        log.info("Autonomous init");
        if(this.autonomousCommand != null) {
            log.info("Starting command: " + this.autonomousCommand);
            this.autonomousCommand.start();
        } else {
            log.warn("No autonomousCommand set.");
        }
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        this.sharedPeriodic();
    }

    public void teleopInit() {
        log.info("Teleop init");
        if(this.autonomousCommand != null) {
            log.info("Cancelling autonomousCommand.");
            this.autonomousCommand.cancel();
        }
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        this.sharedPeriodic();
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
    
    protected void sharedPeriodic() {
        xScheduler.run();
        this.updatePeriodicDataSources();
    }
    
    protected void registerPeriodicDataSource(PeriodicDataSource telemetrySource) {
        this.periodicDataSources.add(telemetrySource);
    }
    
    protected void updatePeriodicDataSources() {
        for (PeriodicDataSource periodicDataSource: this.periodicDataSources) {
            periodicDataSource.updatePeriodicData();
        }
    }
}
