package xbot.common.command;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.simulation.DriverStationSim;

import org.apache.logging.log4j.LogManager;
import org.json.JSONObject;
import org.littletonrobotics.junction.LogFileUtil;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGReader;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.sensors.XTimer;
import xbot.common.controls.sensors.XTimerImpl;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.components.BaseComponent;
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
public abstract class BaseRobot extends LoggedRobot {

    org.apache.logging.log4j.Logger log;

    protected XPropertyManager propertyManager;
    protected XScheduler xScheduler;

    // Other than initially creating required systems, you should never use the injector again
    private BaseComponent injectorComponent;

    protected Command autonomousCommand;
    protected AutonomousCommandSelector autonomousCommandSelector;

    protected WebotsClient webots;
    protected DevicePolice devicePolice;
    protected SimulationPayloadDistributor simulationPayloadDistributor;

    protected List<DataFrameRefreshable> dataFrameRefreshables = new ArrayList<>();

    boolean forceWebots = true; // TODO: figure out a better way to swap between simulation and replay.

    public BaseRobot() {
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
     * @return an implementation of BaseComponent that will be used throughout the robot.
     */
    protected BaseComponent getInjectorComponent() {
        return injectorComponent;
    }

    /**
     * This function is run when the robot is first started up and should be used for any initialization code.
     */
    public void robotInit() {

        Logger.getInstance().recordMetadata("ProjectName", "XbotProject"); // Set a metadata value
        if (isReal() || forceWebots) {
            Logger.getInstance().addDataReceiver(new WPILOGWriter("/media/sda1/")); // Log to a USB stick
            Logger.getInstance().addDataReceiver(new NT4Publisher()); // Publish data to NetworkTables
            new PowerDistribution(1, PowerDistribution.ModuleType.kRev); // Enables power distribution logging
        } else {
            setUseTiming(false); // Run as fast as possible
            String logPath = LogFileUtil.findReplayLog(); // Pull the replay log from AdvantageScope (or prompt the user)
            Logger.getInstance().setReplaySource(new WPILOGReader(logPath)); // Read replay log
            Logger.getInstance().addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim"))); // Save outputs to a new log
        }

        Logger.getInstance().start(); // Start logging! No more data receivers, replay sources, or metadata values may be added.
        DriverStation.silenceJoystickConnectionWarning(true);

        
        log = LogManager.getLogger(BaseRobot.class);
        log.info("========== BASE ROBOT INITIALIZING ==========");
        setupInjectionModule();
        log.info("========== INJECTOR CREATED ==========");
        this.initializeSystems();
        log.info("========== SYSTEMS INITIALIZED ==========");
        SmartDashboard.putData(CommandScheduler.getInstance());

        if (this.isReal()) {
            // We're just so tired of seeing these in logs. We may re-enable this at competition time.
            DriverStation.silenceJoystickConnectionWarning(true);
        }
        PropertyFactory pf = injectorComponent.propertyFactory();

        devicePolice = injectorComponent.devicePolice();
        if (forceWebots) {
            simulationPayloadDistributor = injectorComponent.simulationPayloadDistributor();
        }
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
    }

    protected void initializeSystems() {
        updateLoggingContext();
        // override with additional systems (but call this one too)
        XTimerImpl timerimpl = injectorComponent.timerImplementation();
        XTimer.setImplementation(timerimpl);

        // Get the property manager and get all properties from the robot disk
        propertyManager = injectorComponent.propertyManager();
        xScheduler = injectorComponent.scheduler();
        // All this does is set the timeout period for the scheduler - the actual loop still runs at 50hz.
        CommandScheduler.getInstance().setPeriod(0.05);
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

    double outsidePeriodicStart = 0;
    
    protected void sharedPeriodic() {
        double outsidePeriodicEnd = getPerformanceTimestampInMs();
        Logger.getInstance().recordOutput("OutsidePeriodicMs", outsidePeriodicEnd - outsidePeriodicStart);
        // Get a fresh data frame from all top-level components (typically large subsystems or shared sensors)
        double dataFrameStart = getPerformanceTimestampInMs();
        for (DataFrameRefreshable refreshable : dataFrameRefreshables) {
            refreshable.refreshDataFrame();
        }
        double dataFrameEnd = getPerformanceTimestampInMs();
        Logger.getInstance().recordOutput("RefreshDataFrameMs", dataFrameEnd - dataFrameStart);

        double schedulerStart = getPerformanceTimestampInMs();
        xScheduler.run();
        double schedulerEnd = getPerformanceTimestampInMs();
        Logger.getInstance().recordOutput("SchedulerMs", schedulerEnd - schedulerStart);
        
        outsidePeriodicStart = getPerformanceTimestampInMs();
    }

    
    @Override
    public void simulationInit() {
        // TODO: Add something to detect replay vs Webots, and skip all of this if we're in replay mode.
        /*if (forceWebots) {
            webots = injectorComponent.webotsClient();
            webots.initialize();
            DriverStationSim.setEnabled(true);
        }*/
    }

    @Override
    public void simulationPeriodic() {
        // TODO: Add something to detect replay vs Webots, and skip all of this if we're in replay mode.
        /*if (forceWebots) {
            // find all simulatable motors
            List<JSONObject> motors = new ArrayList<JSONObject>();

            for (String deviceId : devicePolice.registeredChannels.keySet()) {
                Object device = devicePolice.registeredChannels.get(deviceId);
                if (device instanceof ISimulatableMotor) {
                    motors.add(((ISimulatableMotor) device).getSimulationData());
                }
                if (device instanceof ISimulatableSolenoid) {
                    motors.add(((ISimulatableSolenoid) device).getSimulationData());
                }
            }
            JSONObject response = webots.sendMotors(motors);

            simulationPayloadDistributor.distributeSimulationPayload(response);
        }*/
    }

    private double getPerformanceTimestampInMs() {
        return org.littletonrobotics.junction.Logger.getInstance().getRealTimestamp()*1.0 / 1000.0;

    }
}
