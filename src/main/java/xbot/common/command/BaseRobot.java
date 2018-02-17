package xbot.common.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import xbot.common.injection.RobotModule;
import xbot.common.logging.TimeLogger;
import xbot.common.logic.Latch;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

/**
 * Core Robot class which configures logging, properties,
 * scheduling, and the injector. Required for a fair amount
 * of CommonLib functionality.
 */
public class BaseRobot extends TimedRobot {

    static Logger log = Logger.getLogger(BaseRobot.class);
    Latch brownoutLatch = new Latch(false, EdgeType.Both);

    protected XPropertyManager propertyManager;
    protected XScheduler xScheduler;

    protected AbstractModule injectionModule;

    // Other than initially creating required systems, you should never use the injector again
    protected Injector injector;
    
    protected Command autonomousCommand;
    
    protected DoubleProperty frequencyReportInterval;
    protected double lastFreqCounterResetTime = -1;
    protected int loopCycleCounter = 0;
    
    protected Map<PeriodicDataSource, TimeLogger> sourceAndTimers;
    TimeLogger schedulerMonitor;
    TimeLogger outsidePeriodicMonitor;

    public BaseRobot() {
        super();
        setupInjectionModule();
        sourceAndTimers = new HashMap<PeriodicDataSource, TimeLogger>();
        
        brownoutLatch.addObserver((Observable o, Object arg) -> {
            if(arg instanceof EdgeType) {
                EdgeType edge = (EdgeType)arg;
                if(edge == EdgeType.RisingEdge) {
                    log.warn("Entering brownout");
                }
                else if(edge == EdgeType.FallingEdge) {
                    log.info("Leaving brownout");
                }
            }
        });
        
    }
    
    /**
     * Override if you need a different module
     */
    protected void setupInjectionModule() {
        this.injectionModule = new RobotModule();
    }

    /**
     * This function is run when the robot is first started up and should be used for any initialization code.
     */
    public void robotInit() {

        // Get our logging config
        try {
            DOMConfigurator.configure("/home/lvuser/log4j.xml");
        } catch (Exception e) {
            // Had a problem loading the config. Robot should continue!
            final String errorString = "Couldn't configure logging - file probably missing or malformed";
            System.out.println(errorString);
            DriverStation.reportError(errorString, false);
        }

        this.injector = Guice.createInjector(this.injectionModule);
        this.initializeSystems();
        SmartDashboard.putData(Scheduler.getInstance());
        
        frequencyReportInterval = injector.getInstance(XPropertyManager.class).createPersistentProperty("Robot loop frequency report interval", 20);
        schedulerMonitor = new TimeLogger("XScheduler", (int)frequencyReportInterval.get());
        outsidePeriodicMonitor = new TimeLogger("OutsidePeriodic", 20);
    }

    protected void initializeSystems() {
        // override with additional systems (but call this one too)

        // Get the property manager and get all properties from the robot disk
        propertyManager = this.injector.getInstance(XPropertyManager.class);
        xScheduler = this.injector.getInstance(XScheduler.class);
    }

    @Override
    public void disabledInit() {
        log.info("Disabled init (" + getMatchContextString() + ")");
        propertyManager.saveOutAllProperties();
    }

    public void disabledPeriodic() {
        this.sharedPeriodic();
    }
    
    protected String getMatchContextString() {
        DriverStation ds = DriverStation.getInstance();
        return ds.getAlliance().toString() + ds.getLocation() + ", "
            + ds.getMatchTime() + "s, "
            + (ds.isDSAttached() ? "DS connected" : "DS disconnected") + ", "
            + (ds.isFMSAttached() ? "FMS connected" : "FMS disconnected") + ", "
            + "Is disabled: " + ds.isDisabled() + ", "
            + "Is enabled: " + ds.isEnabled() + ", "
            + "Is auto: " + ds.isAutonomous() + ", "
            + "Is teleop: " + ds.isOperatorControl() + ", "
            + "Is test: " + ds.isTest() + ", "
            + "Is browned out: " + RobotController.isBrownedOut() + ", "
            + "Is output enabled: " + RobotController.isSysActive() + ", "
            + "Battery voltage: " + RobotController.getBatteryVoltage();
    }

    public void autonomousInit() {
        log.info("Autonomous init (" + getMatchContextString() + ")");
        if(this.autonomousCommand != null) {
            log.info("Starting autonomous command: " + this.autonomousCommand);
            this.autonomousCommand.start();
        } else {
            log.warn("No autonomous command set.");
        }
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        this.sharedPeriodic();
    }

    public void teleopInit() {
        log.info("Teleop init (" + getMatchContextString() + ")");
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
    }
    
    protected void sharedPeriodic() {
        outsidePeriodicMonitor.stop();
        schedulerMonitor.start();
        xScheduler.run();
        schedulerMonitor.stop();
        
        this.updatePeriodicDataSources();
        
        brownoutLatch.setValue(RobotController.isBrownedOut());
        
        loopCycleCounter++;
        double timeSinceLastLog = Timer.getFPGATimestamp() - lastFreqCounterResetTime;
        if(lastFreqCounterResetTime <= 0) {
            lastFreqCounterResetTime = Timer.getFPGATimestamp();
        }
        else if(timeSinceLastLog >= frequencyReportInterval.get()) {
            double loopsPerSecond = loopCycleCounter / timeSinceLastLog; 
            
            loopCycleCounter = 0;
            lastFreqCounterResetTime = Timer.getFPGATimestamp();
            
            log.info("Robot loops per second: " + loopsPerSecond);
        }
        
        outsidePeriodicMonitor.start();
    }
    
    protected void registerPeriodicDataSource(PeriodicDataSource telemetrySource) {
        log.info("Adding periodic watcher for " + telemetrySource.getName());
        sourceAndTimers.put(
                telemetrySource, new TimeLogger(telemetrySource.getName(), 20));
    }
    
    protected void updatePeriodicDataSources() {
        for (PeriodicDataSource periodicDataSource: this.sourceAndTimers.keySet()) {
            TimeLogger monitor = sourceAndTimers.get(periodicDataSource);
            monitor.start();
            periodicDataSource.updatePeriodicData();
            monitor.stop();
        }
    }
}
