package xbot.common.controls.actuators;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
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
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;

import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;


public abstract class XCANTalon implements IMotorControllerEnhanced {
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
    protected PropertyFactory propMan;

    private DoubleProperty currentProperty = null;
    private DoubleProperty outVoltageProperty = null;
    private DoubleProperty temperatureProperty = null;
    private DoubleProperty positionProperty = null;
    private DoubleProperty velocityProperty = null;

    protected String policeTicket;

    public interface XCANTalonFactory {
        XCANTalon create(CANTalonInfo deviceInfo);
    }

    public XCANTalon(int deviceId, PropertyFactory propMan, DevicePolice police) {
        this.deviceId = deviceId;
        this.propMan = propMan;
        policeTicket = police.registerDevice(DeviceType.CAN, deviceId, this);
    }



    public void createTelemetryProperties(String callingSystemPrefix, String deviceName) {
        // Creates nice prefixes for the SmartDashboard.
        propMan.setPrefix(callingSystemPrefix + "/" + deviceName);
        currentProperty = propMan.createEphemeralProperty("current", 0);
        outVoltageProperty = propMan.createEphemeralProperty("voltage", 0);
        temperatureProperty = propMan.createEphemeralProperty("temperature", 0);
        positionProperty = propMan.createEphemeralProperty("position", 0);
        velocityProperty = propMan.createEphemeralProperty("velocity", 0);
    }

    public void updateTelemetryProperties() {
        if(currentProperty == null
                || outVoltageProperty == null
                || temperatureProperty == null
                || positionProperty == null
                || velocityProperty == null) {
            return;
        }

        currentProperty.set(this.getOutputCurrent());
        outVoltageProperty.set(this.getMotorOutputVoltage());
        temperatureProperty.set(this.getTemperature());

        positionProperty.set(this.getSelectedSensorPosition(0));
        velocityProperty.set(this.getSelectedSensorVelocity(0));
    }
    /*
    @Override
    public int hashCode() {
        return this.deviceId;
    }*/
    /*
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof XCANTalon) {
            // This works since our hash code IS our device ID.
            return ((XCANTalon)obj).hashCode() == this.hashCode();
          }
        return false;
    }*/

 // ------ Set output routines. ----------//
    public abstract void set(ControlMode Mode, double demand);

    public void set(ControlMode mode, double demand1, double demand2) {
        // Deliberately do nothing.
    }

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
    public abstract double getSelectedSensorPosition(int pidIdx);

    public abstract double getSelectedSensorVelocity(int pidIdx);

    public abstract ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs);

    // ------ status frame period changes ----------//
    public abstract ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs);

    public abstract ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs);
    public abstract ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs );

    public abstract int getStatusFramePeriod(StatusFrame frame, int timeoutMs);
    public abstract int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs );

    //----- velocity signal conditionaing ------//
    public abstract ErrorCode configVelocityMeasurementPeriod(SensorVelocityMeasPeriod period, int timeoutMs );
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

    public abstract boolean isFwdLimitSwitchClosed();
    public abstract boolean isRevLimitSwitchClosed();

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

    public abstract double getClosedLoopError(int pidIdx);

    public abstract double getIntegralAccumulator(int pidIdx) ;

    public abstract double getErrorDerivative(int pidIdx) ;

    public abstract void selectProfileSlot(int slotIdx, int pidIdx);

    //public abstract int getClosedLoopTarget(int pidIdx); // will be added to JNI

    public abstract double getActiveTrajectoryPosition();

    public abstract double getActiveTrajectoryVelocity();

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

    public void simpleSet(double percentInput) {
    	set(ControlMode.PercentOutput, percentInput);
    }

	public void stopMotor() {
		neutralOutput();
	}

    /***
     * Convenience function to rapidly configure two CANTalons to work in tandem; often used for drive motors.
     * @param prefix Prefix for network tables; typically, fill this with getPrefix() if calling this from a Subsystem or Command.
     * @param masterName Motor name for network tables
     * @param master Talon that will control overall operations
     * @param follower Talon that will follow the master
     * @param masterInverted Should the master be inverted?
     * @param followerInverted Should the follower be inverted RELATIVE TO THE MASTER?
     * @param sensorPhase Is the encoder in phase with the master?
     */
    public static void configureMotorTeam(String prefix, String masterName, XCANTalon master, XCANTalon follower, boolean masterInverted,
            boolean followerInverted, boolean sensorPhase) {
        master.configureAsMasterMotor(prefix, masterName, masterInverted, sensorPhase);
        follower.configureAsFollowerMotor(master, followerInverted);
    }

    public static void configureMotorTeam(String prefix, String masterName, XCANTalon master, XCANTalon follower1, XCANTalon follower2, boolean masterInverted,
            boolean follower1Inverted, boolean follower2Inverted, boolean sensorPhase) {
        master.configureAsMasterMotor(prefix, masterName, masterInverted, sensorPhase);
        follower1.configureAsFollowerMotor(master, follower1Inverted);
        follower2.configureAsFollowerMotor(master, follower2Inverted);
    }

    public static void configureMotorTeam(String prefix, String masterName, XCANTalon master, XCANTalon follower1, XCANTalon follower2, XCANTalon follower3, boolean masterInverted,
            boolean follower1Inverted, boolean follower2Inverted, boolean follower3Inverted, boolean sensorPhase) {
        master.configureAsMasterMotor(prefix, masterName, masterInverted, sensorPhase);
        follower1.configureAsFollowerMotor(master, follower1Inverted);
        follower2.configureAsFollowerMotor(master, follower2Inverted);
        follower3.configureAsFollowerMotor(master, follower3Inverted);
    }

    /**
     * Convenience function to rapidly configure a CANTalon as a Master motor. Uses some typical configurations that can be
     * overriden later (for example, it sets typical maximum/minimum output values to 1 and -1)
     * @param prefix Prefix for network tables; typically, fill this with getPrefix() if calling this from a Subsystem or Command.
     * @param masterName Motor name for network tables
     * @param masterInverted Should the master be inverted?
     * @param sensorPhase Is the encoder in phase with the master?
     */
    public void configureAsMasterMotor(String prefix, String masterName, boolean masterInverted, boolean sensorPhase) {
        this.setInverted(masterInverted);
        this.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        this.setSensorPhase(sensorPhase);
        this.createTelemetryProperties(prefix, masterName);

        this.setNeutralMode(NeutralMode.Coast);
        this.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
        this.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);

        this.configPeakOutputForward(1, 0);
        this.configPeakOutputReverse(-1, 0);

        this.configForwardSoftLimitEnable(false, 0);
        this.configReverseSoftLimitEnable(false, 0);
    }

    /**
     * Convenience function to rapidly configure a XCANTalon to follow another XCANTalon. Uses some typical configurations that can be
     * overriden later (for example, it sets typical maximum/minimum output values to 1 and -1)
     * @param master The master XCANTalon that this should follow
     * @param followerInverted Should the follower be inverted RELATIVE TO THE MASTER?
     */
    public void configureAsFollowerMotor(XCANTalon master, boolean followerInverted) {
        this.follow(master);
        this.setInverted(followerInverted);

        this.setNeutralMode(NeutralMode.Coast);
        this.configPeakOutputForward(1, 0);
        this.configPeakOutputReverse(-1, 0);

        this.configForwardLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);
        this.configReverseLimitSwitchSource(LimitSwitchSource.Deactivated, LimitSwitchNormal.Disabled, 0);

        this.configForwardSoftLimitEnable(false, 0);
        this.configReverseSoftLimitEnable(false, 0);
    }

    public abstract int getPulseWidthRiseToFallUs();
}
