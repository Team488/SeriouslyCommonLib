package xbot.common.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.json.JSONObject;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import xbot.common.controls.sensors.XTimer;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.components.BaseComponent;
import xbot.common.logging.RobotSession;
import xbot.common.logging.TimeLogger;
import xbot.common.logic.Latch;
import xbot.common.logic.Latch.EdgeType;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.XPropertyManager;
import xbot.common.simulation.ISimulatableMotor;
import xbot.common.simulation.ISimulatableSolenoid;
import xbot.common.simulation.SimulationPayloadDistributor;
import xbot.common.simulation.WebotsClient;
import xbot.common.subsystems.autonomous.AutonomousCommandSelector;

/**
 * Core Robot class which configures logging, properties,
 * scheduling, and the injector. Required for a fair amount
 * of CommonLib functionality.
 */
public abstract class BaseRobot extends TimedRobot {

    Logger log;
    Latch brownoutLatch;

    protected XPropertyManager propertyManager;
    protected XScheduler xScheduler;

    // Other than initially creating required systems, you should never use the injector again
    private BaseComponent injectorComponent;

    protected Command autonomousCommand;
    protected AutonomousCommandSelector autonomousCommandSelector;
    
    protected DoubleProperty batteryVoltage;
    protected DoubleProperty frequencyReportInterval;
    protected double lastFreqCounterResetTime = -1;
    protected int loopCycleCounter = 0;

    protected WebotsClient webots;
    protected DevicePolice devicePolice;
    protected SimulationPayloadDistributor simulationPayloadDistributor;
    
    TimeLogger schedulerMonitor;
    TimeLogger outsidePeriodicMonitor;

    protected RobotSession robotSession;

    public BaseRobot() {                
        brownoutLatch = new Latch(false, EdgeType.Both, edge -> {
            if(edge == EdgeType.RisingEdge) {
                log.warn("Entering brownout");
            }
            else if(edge == EdgeType.FallingEdge) {
                log.info("Leaving brownout");
            }
        });
        
    }
    
    /**
     * Override if you need a different module
     */
    protected void setupInjectionModule() {
        injectorComponent = createDaggerComponent();
    }

    /**
     * Returns the {@link BaseComponent} instance used for dependency injection
     */
    protected abstract BaseComponent createDaggerComponent();

    /**
     * Get the dependency injection component
     * @return
     */
    protected BaseComponent getInjectorComponent() {
        return injectorComponent;
    }

    /**
     * This function is run when the robot is first started up and should be used for any initialization code.
     */
    public void robotInit() {

        // Get our logging config
        try {
            if(BaseRobot.isReal()) {
                DOMConfigurator.configure("/home/lvuser/deploy/log4j.xml");
            } else {
                DOMConfigurator.configure("SeriouslyCommonLib/lib/log4jConfig/log4j4unitTesting.xml");
            }
        } catch (Exception e) {
            // Had a problem loading the config. Robot should continue!
            final String errorString = "Couldn't configure logging - file probably missing or malformed";
            System.out.println(errorString);
            DriverStation.reportError(errorString, false);
        }
        
        log = Logger.getLogger(BaseRobot.class);
        log.info("========== BASE ROBOT INITIALIZING ==========");
        setupInjectionModule();
        log.info("========== INJECTOR CREATED ==========");
        this.initializeSystems();
        log.info("========== SYSTEMS INITIALIZED ==========");
        SmartDashboard.putData(CommandScheduler.getInstance());
        
        PropertyFactory pf = injectorComponent.propertyFactory();
        pf.setTopLevelPrefix();
        frequencyReportInterval = pf.createPersistentProperty("Robot loop frequency report interval", 20);
        batteryVoltage = pf.createEphemeralProperty("Battery Voltage", 0);
        schedulerMonitor = new TimeLogger("XScheduler", (int)frequencyReportInterval.get());
        outsidePeriodicMonitor = new TimeLogger("OutsidePeriodic", 20);
        robotSession = injectorComponent.robotSession();
        devicePolice = injectorComponent.devicePolice();
        simulationPayloadDistributor = injectorComponent.simulationPayloadDistributor();
        LiveWindow.disableAllTelemetry();
    }
    
    protected String getEnableTypeString() {
        if (!DriverStation.isEnabled()) {
            return "disabled";
        }
        
        if (DriverStation.isAutonomous()) {
            return "auto";
        }
        
        if (DriverStation.isTeleop()) {
            return "teleop";
        }
        
        if (DriverStation.isTest()) {
            return "test";
        }
        
        return "enabled/unknown";
    }
    
    protected void updateLoggingContext() {
        String dsStatus = DriverStation.isDSAttached() ? "DS" : "no DS";
        String fmsStatus = DriverStation.isFMSAttached() ? "FMS" : "no FMS";
        String matchStatus = DriverStation.getMatchType().toString() + " " + DriverStation.getMatchNumber() + " " + DriverStation.getReplayNumber();
        String enableStatus = getEnableTypeString();
        String matchContext = dsStatus + ", " + fmsStatus + ", " + enableStatus + ", " + matchStatus;
        org.apache.log4j.MDC.put("matchContext", matchContext);
    }

    protected void initializeSystems() {
        updateLoggingContext();
        // override with additional systems (but call this one too)
        XTimerImpl timerimpl = injectorComponent.timerImplementation();
        XTimer.setImplementation(timerimpl);

        // Get the property manager and get all properties from the robot disk
        propertyManager = injectorComponent.propertyManager();
        xScheduler = injectorComponent.scheduler();
        autonomousCommandSelector = injectorComponent.autonomousCommandSelector();
    }

    @Override
    public void disabledInit() {
        updateLoggingContext();
        log.info("Disabled init (" + getMatchContextString() + ")");
        propertyManager.saveOutAllProperties();
    }

    public void disabledPeriodic() {
        this.sharedPeriodic();
    }
    
    protected String getMatchContextString() {
        return DriverStation.getAlliance().toString() + DriverStation.getLocation() + ", "
            + DriverStation.getMatchTime() + "s, "
            + (DriverStation.isDSAttached() ? "DS connected" : "DS disconnected") + ", "
            + (DriverStation.isFMSAttached() ? "FMS connected" : "FMS disconnected") + ", "
            + "Is disabled: " + DriverStation.isDisabled() + ", "
            + "Is enabled: " + DriverStation.isEnabled() + ", "
            + "Is auto: " + DriverStation.isAutonomous() + ", "
            + "Is teleop: " + DriverStation.isTeleop() + ", "
            + "Is test: " + DriverStation.isTest() + ", "
            + "Is browned out: " + RobotController.isBrownedOut() + ", "
            + "Is output enabled: " + RobotController.isSysActive() + ", "
            + "Battery voltage: " + RobotController.getBatteryVoltage();
    }

    public void autonomousInit() {
        robotSession.autoInit();
        updateLoggingContext();
        log.info("Autonomous init (" + getMatchContextString() + ")");
        this.autonomousCommand = this.autonomousCommandSelector.getCurrentAutonomousCommand();
        if(this.autonomousCommand != null) {
            log.info("Starting autonomous command: " + this.autonomousCommand);
            this.autonomousCommand.schedule();
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
        robotSession.teleopInit();
        updateLoggingContext();
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

        batteryVoltage.set(RobotController.getBatteryVoltage());
        
        brownoutLatch.setValue(RobotController.isBrownedOut());
        
        loopCycleCounter++;
        double timeSinceLastLog = XTimer.getFPGATimestamp() - lastFreqCounterResetTime;
        if(lastFreqCounterResetTime <= 0) {
            lastFreqCounterResetTime = XTimer.getFPGATimestamp();
        }
        else if(timeSinceLastLog >= frequencyReportInterval.get()) {
            double loopsPerSecond = loopCycleCounter / timeSinceLastLog; 
            
            loopCycleCounter = 0;
            lastFreqCounterResetTime = XTimer.getFPGATimestamp();
            
            log.info("Robot loops per second: " + loopsPerSecond);
        }
        
        outsidePeriodicMonitor.start();
    }

    
    @Override
    public void simulationInit() {
        webots = injectorComponent.webotsClient();
        webots.initialize();
    }

    @Override
    public void simulationPeriodic() {
        // find all simulatable motors
        List<JSONObject> motors = new ArrayList<JSONObject>();
        
        for (String deviceId: devicePolice.registeredChannels.keySet()) {
            Object device = devicePolice.registeredChannels.get(deviceId);
            if (device instanceof ISimulatableMotor) {
                motors.add(((ISimulatableMotor)device).getSimulationData());
            }
            if (device instanceof ISimulatableSolenoid) {
                motors.add(((ISimulatableSolenoid)device).getSimulationData());
            }
        }
        JSONObject response = webots.sendMotors(motors);

        simulationPayloadDistributor.distributeSimulationPayload(response);
    }
}
