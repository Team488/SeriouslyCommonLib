package xbot.common.subsystems;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Timer;
import xbot.common.command.BaseSubsystem;
import xbot.common.command.PeriodicDataSource;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.math.PIDPropertyManager;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

/**
 * We have a few Subsystems that have one primary goal:
 * -Get a motor to rotate at a specific controlled speed. 
 * -Occasionally set the motor to use a specific power.
 * This requires a decent amount of overhead in terms of Properties and 
 * other configuration, and is something that could be mostly delegated to this class,
 * as the only significant point of difference is how many motors are controlled
 * by the Subsystem.
 * 
 * If the system has more than one motor it is typically
 * configured into follower mode, so almost no extra configuration is needed.
 * @author John
 *
 */
public abstract class BaseXCANTalonSpeedControlledSubsystem extends BaseSubsystem implements PeriodicDataSource {
    
    protected final XCANTalon masterMotor;
    protected XCANTalon followerMotor;
       
    // output telemetry properties
    protected final DoubleProperty systemCurrentSpeed;
    protected final DoubleProperty systemTargetSpeed;
    protected final DoubleProperty systemOutputPower;
    protected final DoubleProperty systemTalonError;
    protected final BooleanProperty atSpeedProp;
    protected final BooleanProperty enablesystemLogging;
    
    private final DoubleProperty systemSpeedThresh;
    protected final PIDPropertyManager pidPropertyManager;
    protected final int masterChannel;
        
    /**
     * 
     * @param name What the system is called. This will apply to various Properties.
     * @param masterChannel The CAN index of the master motor (or the only motor, for a simple system)
     * @param factory The WPIFactory
     * @param pidPropertyManager The default PIDF values the system should use
     * @param propManager The XPropertyManager
     */
    public BaseXCANTalonSpeedControlledSubsystem(
            String name,
            int masterChannel,
            boolean invertMaster,
            boolean invertMasterSensor,
            WPIFactory factory, 
            PIDPropertyManager pidPropertyManager,
            XPropertyManager propManager){
        super(name);
        log.info("Creating");
        
        this.pidPropertyManager = pidPropertyManager;
        this.masterChannel = masterChannel;
        
        // Reading through the manual, it looks like only a few things 
        // support human units. It might be easier to just keep everything in native units per 100ms,
        // which is what the Talon uses for all its calculations.
        // I call this Ticks per Deciseconds, or TPD
        systemSpeedThresh = propManager.createPersistentProperty(name + " nominal speed thresh (TPD)", 1);
        systemCurrentSpeed = propManager.createEphemeralProperty(name + " current speed (TPD)", 0);
        systemTargetSpeed = propManager.createEphemeralProperty(name + " goal speed (TPD)", 0);
        systemOutputPower = propManager.createEphemeralProperty(name + " voltage", 0);
        atSpeedProp = propManager.createEphemeralProperty("Is" + name + " at speed?", false);
        systemTalonError = propManager.createEphemeralProperty(name + " speed error", 0);
        enablesystemLogging = propManager.createEphemeralProperty("Is " + name + " logging enabled?", false);
        
        masterMotor = factory.getCANTalonSpeedController(masterChannel);
        initializeMasterMotorConfiguration(invertMaster, invertMasterSensor);
        masterMotor.createTelemetryProperties(name + "  master", propManager);
    }
    
    protected void initializeMasterMotorConfiguration(boolean motorInverted, boolean motorSensorInverted) {
        masterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        masterMotor.setInverted(motorInverted);
        masterMotor.reverseSensor(motorSensorInverted);
        masterMotor.setControlMode(TalonControlMode.Speed);
        masterMotor.setProfile(0);
    }
        
    private void updateMotorPidValues() {
        masterMotor.setP(pidPropertyManager.getP());
        masterMotor.setI(pidPropertyManager.getI());
        masterMotor.setD(pidPropertyManager.getD());
        masterMotor.setF(pidPropertyManager.getF());
    }
    
    /**
     * Sets the output power of the system directly - no PID of any kind
     * @param Power is set to system
     */
    public void setPower(double power) {
        masterMotor.ensureTalonControlMode(TalonControlMode.PercentVbus);
        masterMotor.set(power);
    }
    
    /**
     * Returns the current "robot power" (e.g. -1 == full reverse, 1 == full forward)
     * @return Last Motor power
     */
    public double getPower() {
        double robotPower = masterMotor.getOutputVoltage() / masterMotor.getBusVoltage();
        double inversionFactor = masterMotor.getInverted() ? -1 : 1;
        return robotPower * inversionFactor;
    }

    /**
     * Gives the system a new speed goal.
     * @param systemTargetSpeed in Rotations per Second (RPS)
     */
    public void setTargetSpeed(double speed) {
        masterMotor.ensureTalonControlMode(TalonControlMode.Speed);
        
        // Update property for dashboard
        systemTargetSpeed.set(speed);
        // If there have been any changes to encoder settings or PID setings, apply them.
        updateMotorPidValues();
        // Instruct motor about new speed goal
        masterMotor.set(speed);
    }
    
    public double getSpeed() {
        return masterMotor.getSpeed();
    }
    
    public boolean isAtSpeed() {
        return Math.abs(getSpeed() - systemTargetSpeed.get()) <= systemSpeedThresh.get();
    }
    
    public double getTargetSpeed() {
        return systemTargetSpeed.get();
    }

    public void updatePeriodicData() {
        masterMotor.updateTelemetryProperties();        
        atSpeedProp.set(isAtSpeed());
        systemCurrentSpeed.set(getSpeed());
        systemOutputPower.set(masterMotor.getOutputVoltage() / masterMotor.getBusVoltage());
        systemTalonError.set(masterMotor.getClosedLoopError());
        
        if(enablesystemLogging.get()){
            double currentTime = Timer.getFPGATimestamp();
            // Format: time, voltage, error, speed
            log.info(currentTime + "," 
                 + systemOutputPower.get() + "," 
                 + systemTalonError.get() + ","
                 + systemCurrentSpeed.get());
        }
    }
}