package xbot.common.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.actuators.XCANTalon.XCANTalonFactory;
import xbot.common.controls.sensors.XTimer;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.math.PIDPropertyManager;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

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
public abstract class BaseXCANTalonSpeedControlledSubsystem extends BaseSubsystem {
    
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
     * @param propManager The PropertyFactory
     */
    public BaseXCANTalonSpeedControlledSubsystem(
            String name,
            int masterChannel,
            boolean invertMaster,
            boolean invertMasterSensor,
            XCANTalonFactory factory, 
            PIDPropertyManager pidPropertyManager,
            PropertyFactory propManager){
        super();
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
        
        masterMotor = factory.create(new CANTalonInfo(masterChannel, false));
        initializeMasterMotorConfiguration(invertMaster, invertMasterSensor);
        masterMotor.createTelemetryProperties(name + "/", name + "  master");
    }
    
    protected void initializeMasterMotorConfiguration(boolean motorInverted, boolean motorSensorInverted) {
        masterMotor.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
        
        masterMotor.setInverted(motorInverted);
        masterMotor.setSensorPhase(motorSensorInverted);
    }
        
    private void updateMotorPidValues() {        
        masterMotor.config_kP(0, pidPropertyManager.getP(), 0);
        masterMotor.config_kI(0, pidPropertyManager.getI(), 0);
        masterMotor.config_kD(0, pidPropertyManager.getD(), 0);
        masterMotor.config_kF(0, pidPropertyManager.getF(), 0);
    }
    
    /**
     * Sets the output power of the system directly - no PID of any kind
     * @param Power is set to system
     */
    public void setPower(double power) {
        masterMotor.set(ControlMode.PercentOutput, power);
    }
    
    /**
     * Returns the current "robot power" (e.g. -1 == full reverse, 1 == full forward)
     * @return Last Motor power
     */
    public double getPower() {
        double robotPower = masterMotor.getMotorOutputVoltage() / masterMotor.getBusVoltage();
        double inversionFactor = masterMotor.getInverted() ? -1 : 1;
        return robotPower * inversionFactor;
    }

    /**
     * Gives the system a new speed goal.
     * @param systemTargetSpeed in Rotations per Second (RPS)
     */
    public void setTargetSpeed(double speed) {        
        // Update property for dashboard
        systemTargetSpeed.set(speed);
        // If there have been any changes to encoder settings or PID setings, apply them.
        updateMotorPidValues();
        // Instruct motor about new speed goal
        masterMotor.set(ControlMode.Velocity, speed);
    }
    
    public double getSpeed() {
        return masterMotor.getSelectedSensorVelocity(0);
    }
    
    public boolean isAtSpeed() {
        return Math.abs(getSpeed() - systemTargetSpeed.get()) <= systemSpeedThresh.get();
    }
    
    public double getTargetSpeed() {
        return systemTargetSpeed.get();
    }

    @Override
    public void periodic() {
        masterMotor.updateTelemetryProperties();        
        atSpeedProp.set(isAtSpeed());
        systemCurrentSpeed.set(getSpeed());
        systemOutputPower.set(masterMotor.getMotorOutputVoltage() / masterMotor.getBusVoltage());
        systemTalonError.set(masterMotor.getClosedLoopError(0));
        
        if(enablesystemLogging.get()){
            double currentTime = XTimer.getFPGATimestamp();
            // Format: time, voltage, error, speed
            log.info(currentTime + "," 
                 + systemOutputPower.get() + "," 
                 + systemTalonError.get() + ","
                 + systemCurrentSpeed.get());
        }
    }
}