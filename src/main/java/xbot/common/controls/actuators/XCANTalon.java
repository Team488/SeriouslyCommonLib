package xbot.common.controls.actuators;

import com.ctre.phoenix.*;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;

import edu.wpi.first.wpilibj.MotorSafety;
import edu.wpi.first.wpilibj.MotorSafetyHelper;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.StringProperty;
import xbot.common.properties.XPropertyManager;


public abstract class XCANTalon extends SendableBase implements IMotorControllerEnhanced, MotorSafety {
    /*
     * Functions currently omitted:
     * 
     * - enableControl/disableControl
     * - delete
     * - motion control frame mode
     * - "expiration" and other MotorSafety members
     * - getPinStateQuad*
     * - getPulseWidth*
     * - setPID w/ special args
     * - setParameter/getParameter
     */
    
    protected int deviceId;
    protected XPropertyManager propMan;
    
    private StringProperty controlModeProperty = null;
    private DoubleProperty currentProperty = null;
    private DoubleProperty outVoltageProperty = null;
    private DoubleProperty temperatureProperty = null;
    private DoubleProperty positionProperty = null;
    private DoubleProperty velocityProperty = null;
    
    private MotorSafetyHelper _safetyHelper;
    private String _description;
    
    public XCANTalon(int deviceId, XPropertyManager propMan) {
        this.deviceId = deviceId;
        this.propMan = propMan;
        
        _safetyHelper = new MotorSafetyHelper(this);
		_safetyHelper.setExpiration(0.0);
		_safetyHelper.setSafetyEnabled(false);
        
        LiveWindow.add(this);
        setName("Talon SRX ", deviceId);
        
        _description = "Talon SRX " + deviceId;
    }
    
    
    
    public void createTelemetryProperties(String deviceName) {
        currentProperty = propMan.createEphemeralProperty(deviceName + " current", 0);
        outVoltageProperty = propMan.createEphemeralProperty(deviceName + " voltage", 0);
        temperatureProperty = propMan.createEphemeralProperty(deviceName + " temperature", 0);
        positionProperty = propMan.createEphemeralProperty(deviceName + " position", 0);
        velocityProperty = propMan.createEphemeralProperty(deviceName + " velocity", 0);
    }
    
    public void updateTelemetryProperties() {
        if(controlModeProperty == null
                || currentProperty == null
                || outVoltageProperty == null
                || temperatureProperty == null) {
            return;
        }
        
        currentProperty.set(this.getOutputCurrent());
        outVoltageProperty.set(this.getMotorOutputVoltage());
        temperatureProperty.set(this.getTemperature());
        
        positionProperty.set(this.getSelectedSensorPosition(0));
        velocityProperty.set(this.getSelectedSensorVelocity(0));
    }
    
    @Override
    public int hashCode() {
        return this.deviceId;
    }

 // ------ Set output routines. ----------//
    public abstract void set(ControlMode Mode, double demand);

    public abstract void set(ControlMode Mode, double demand0, double demand1);

    public abstract void neutralOutput();

    public abstract void setNeutralMode(NeutralMode neutralMode);

    // ------ Invert behavior ----------//
    public abstract void setSensorPhase(boolean PhaseSensor);

    public abstract void setInverted(boolean invert);

    public abstract boolean getInverted();

    // ----- general output shaping ------------------//
    public abstract ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs);

    public abstract ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs);

    public abstract ErrorCode configPeakOutputForward(double percentOut, int timeoutMs);

    public abstract ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs);

    public abstract ErrorCode configNominalOutputForward(double percentOut, int timeoutMs);

    public abstract ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs);

    public abstract ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs);

    // ------ Voltage Compensation ----------//
    public abstract ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs);

    public abstract ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs);

    public abstract void enableVoltageCompensation(boolean enable);

    // ------ General Status ----------//
    public abstract double getBusVoltage() ;

    public abstract double getMotorOutputPercent() ;

    public abstract double getMotorOutputVoltage() ;

    public abstract double getOutputCurrent() ;

    public abstract double getTemperature() ;

    // ------ sensor selection ----------//
    public abstract ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs);

    public abstract ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal,
            int timeoutMs);

    public abstract ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs);
    
    public abstract ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs );

    // ------- sensor status --------- //
    public abstract int getSelectedSensorPosition(int pidIdx);

    public abstract int getSelectedSensorVelocity(int pidIdx);

    public abstract ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs);
    
    // ------ status frame period changes ----------//
    public abstract ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs);

    public abstract ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs);
    public abstract ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs );
    
    public abstract int getStatusFramePeriod(StatusFrame frame, int timeoutMs);
    public abstract int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs );    

    //----- velocity signal conditionaing ------//
    public abstract ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs );
    public abstract ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs );

    //------ remote limit switch ----------//
    public abstract ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs);

    public abstract ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs);

    public abstract void overrideLimitSwitchesEnable(boolean enable);

    // ------ local limit switch ----------//
    public abstract ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs );
    public abstract ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose, int timeoutMs );


    // ------ soft limit ----------//
    public abstract ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit, int timeoutMs);

    public abstract ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit, int timeoutMs);
    
    public abstract ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs);

    public abstract ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs);

    public abstract void overrideSoftLimitsEnable(boolean enable);

    // ------ Current Lim ----------//
    public abstract ErrorCode configPeakCurrentLimit(int amps, int timeoutMs );
    public abstract ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs );
    public abstract ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs );
    public abstract void enableCurrentLimit(boolean enable);

    // ------ General Close loop ----------//
    public abstract ErrorCode config_kP(int slotIdx, double value, int timeoutMs);

    public abstract ErrorCode config_kI(int slotIdx, double value, int timeoutMs);

    public abstract ErrorCode config_kD(int slotIdx, double value, int timeoutMs);

    public abstract ErrorCode config_kF(int slotIdx, double value, int timeoutMs);

    public abstract ErrorCode config_IntegralZone(int slotIdx, int izone, int timeoutMs);

    public abstract ErrorCode configAllowableClosedloopError(int slotIdx, int allowableCloseLoopError, int timeoutMs);

    public abstract ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs);

    //------ Close loop State ----------//
    public abstract ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs);

    public abstract int getClosedLoopError(int pidIdx);

    public abstract double getIntegralAccumulator(int pidIdx) ;

    public abstract double getErrorDerivative(int pidIdx) ;

    public abstract void selectProfileSlot(int slotIdx, int pidIdx);

    //public abstract int getClosedLoopTarget(int pidIdx); // will be added to JNI

    public abstract int getActiveTrajectoryPosition();

    public abstract int getActiveTrajectoryVelocity();

    public abstract double getActiveTrajectoryHeading();

    // ------ Motion Profile Settings used in Motion Magic and Motion Profile
    public abstract ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms, int timeoutMs);

    public abstract ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec, int timeoutMs);

    // ------ Motion Profile Buffer ----------//
    public abstract ErrorCode clearMotionProfileTrajectories();
    public abstract int getMotionProfileTopLevelBufferCount();
    public abstract ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt);
    public abstract boolean isMotionProfileTopLevelBufferFull();
    public abstract void processMotionProfileBuffer();
    public abstract ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill);
    public abstract ErrorCode clearMotionProfileHasUnderrun(int timeoutMs);
    public abstract ErrorCode changeMotionControlFramePeriod(int periodMs);

    // ------ error ----------//
    public abstract ErrorCode getLastError();

    // ------ Faults ----------//
    public abstract ErrorCode getFaults(Faults toFill) ;

    public abstract ErrorCode getStickyFaults(StickyFaults toFill) ;

    public abstract ErrorCode clearStickyFaults(int timeoutMs);

    // ------ Firmware ----------//
    public abstract int getFirmwareVersion();

    public abstract boolean hasResetOccurred();

    // ------ Custom Persistent Params ----------//
    public abstract ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs);

    public abstract int configGetCustomParam(int paramIndex, int timoutMs);

    //------ Generic Param API, typically not used ----------//
    public abstract ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs);
    public abstract ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs);

    public abstract double configGetParameter(ParamEnum paramEnum, int ordinal, int timeoutMs) ;
    public abstract double configGetParameter(int paramEnum, int ordinal, int timeoutMs) ;

    //------ Misc. ----------//
    public abstract int getBaseID();
    public abstract int getDeviceID();

    // ----- Follower ------//
    public abstract void follow(IMotorController masterToFollow);
    public abstract void valueUpdated();
    
    // WPI Compatibility for LiveWindow.
    
    @Override
	public void initSendable(SendableBuilder builder) {
		// TODO Auto-generated method stub
		builder.setSmartDashboardType("CANTalon");
		builder.setSmartDashboardType("Speed Controller");
		builder.setSafeState(this::stopMotor);
		builder.addDoubleProperty("Value", this::getMotorOutputPercent, this::simpleSet);
	}
    
    public void simpleSet(double percentInput) {
    	set(ControlMode.PercentOutput, percentInput);
    }
	
	public void stopMotor() {
		neutralOutput();
	}

	@Override
	public void setExpiration(double timeout) {
		_safetyHelper.setExpiration(timeout);		
	}

	@Override
	public double getExpiration() {
		// TODO Auto-generated method stub
		return _safetyHelper.getExpiration();
	}

	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		return _safetyHelper.isAlive();
	}

	@Override
	public void setSafetyEnabled(boolean enabled) {
		_safetyHelper.setSafetyEnabled(enabled);
	}

	@Override
	public boolean isSafetyEnabled() {
		// TODO Auto-generated method stub
		return _safetyHelper.isSafetyEnabled();
	}

	@Override
	public String getDescription() {
		return _description;
	}
}
