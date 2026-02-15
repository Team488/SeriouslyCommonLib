package xbot.common.command;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

import org.apache.logging.log4j.LogManager;
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
import xbot.common.properties.PropertyFactory;
import xbot.common.properties.XPropertyManager;
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
    protected DataFrameRegistry deviceDataFrameRegistry;
    protected List<DataFrameRefreshable> dataFrameRefreshables = new ArrayList<>();

    boolean forceWebots = true; // TODO: figure out a better way to swap between simulation and replay.

    public Throwable initException = null;

    public BaseRobot() {
    }

    public BaseRobot(double loopInterval) {
        super(loopInterval);
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
        initException = null;
        try {
            Logger.recordMetadata("ProjectName", "XbotProject"); // Set a metadata value
            if (isReal() || forceWebots) {
                var logDirectory = new File("/U/logs");
                if (logDirectory.exists() && logDirectory.isDirectory() && logDirectory.canWrite()) {
                    Logger.addDataReceiver(new WPILOGWriter("/U/logs")); // Log to a USB stick with label LOGSDRIVE plugged into the inner usb port
                }
                Logger.addDataReceiver(new NT4Publisher()); // Publish data to NetworkTables
            } else {
                setUseTiming(false); // Run as fast as possible
                String logPath = LogFileUtil.findReplayLog(); // Pull the replay log from AdvantageScope (or prompt the user)
                Logger.setReplaySource(new WPILOGReader(logPath)); // Read replay log
                Logger.addDataReceiver(new WPILOGWriter(LogFileUtil.addPathSuffix(logPath, "_sim"))); // Save outputs to a new log
            }

            Logger.start(); // Start logging! No more data receivers, replay sources, or metadata values may be added.
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
            deviceDataFrameRegistry = injectorComponent.dataFrameRegistry();

            if (forceWebots) {
                simulationPayloadDistributor = injectorComponent.simulationPayloadDistributor();
            }
            LiveWindow.disableAllTelemetry();
        } catch (Exception e) {
            this.initException = e;
            throw e;
        }
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
        xScheduler.reset();
        // All this does is set the timeout period for the scheduler - the actual loop still runs at 50hz.
        CommandScheduler.getInstance().setPeriod(0.05);
        autonomousCommandSelector = injectorComponent.autonomousCommandSelector();
    }

    @Override
    public void disabledInit() {
        updateLoggingContext();
        propertyManager.refreshDataFrame();
        log.info("Disabled init (" + getMatchContextString() + ")");
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
            CommandScheduler.getInstance().schedule(this.autonomousCommand);
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
        Logger.recordOutput("OutsidePeriodicMs", outsidePeriodicEnd - outsidePeriodicStart);
        // Get a fresh data frame from all top-level components (typically large subsystems or shared sensors)


        // Refresh the properties ahead of all other systems, since some may want to immediately
        // use the relevant values.
        double propertyStart = getPerformanceTimestampInMs();
        propertyManager.refreshDataFrame();
        double propertyEnd = getPerformanceTimestampInMs();
        Logger.recordOutput("RefreshPropertyMs", propertyEnd - propertyStart);

        // Then, refresh any Subsystem or other components that implement DataFrameRefreshable.
        double dataFrameStart = getPerformanceTimestampInMs();
        refreshAllDataFrames();
        double dataFrameEnd = getPerformanceTimestampInMs();
        Logger.recordOutput("RefreshDevicesMs", dataFrameEnd - dataFrameStart);

        double schedulerStart = getPerformanceTimestampInMs();
        xScheduler.run();
        double schedulerEnd = getPerformanceTimestampInMs();
        Logger.recordOutput("SchedulerMs", schedulerEnd - schedulerStart);

        outsidePeriodicStart = getPerformanceTimestampInMs();
    }

    public void refreshAllDataFrames() {
        // all devices are refreshed first, order doesn't matter
        deviceDataFrameRegistry.refreshAll();

        // other things like subsystems refreshed here, they should be done in order
        for (DataFrameRefreshable refreshable : dataFrameRefreshables) {
            refreshable.refreshDataFrame();
        }
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

        // it would be really useful to have some suepr basic non-physical simulation of the robot.
        // As in, if the swerve modules want to go to some angle and speed, we just make that happen directly.
        // If rotation is commanded, we just rotate the robot.


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

    protected double getPerformanceTimestampInMs() {
        return XTimer.getFPGATimestamp() * 1000.0;
    }
}
