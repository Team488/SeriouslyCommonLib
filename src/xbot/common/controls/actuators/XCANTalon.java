package xbot.common.controls.actuators;

import edu.wpi.first.wpilibj.CANTalon;
import xbot.common.properties.XPropertyManager;

public interface XCANTalon extends XSpeedController {
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
    
    // Control and meta config ----------------------------
    boolean isEnabled();
    void enable();
    void disable();
    void setProfile(int profile);
    CANTalon.TalonControlMode getControlMode();
    void setControlMode(CANTalon.TalonControlMode controlMode);
    boolean getBrakeEnableDuringNeutral();
    void setBrakeEnableDuringNeutral(boolean brake);
    void setStatusFrameRateMs(CANTalon.StatusFrameRate stateFrame, int periodMs);
    void reset();
    
    // Meta information -----------------------------------
    int getDeviceID();
    double getOutputCurrent();
    double getOutputVoltage();
    double getTemperature();
    double getBusVoltage();
    long getFirmwareVersion();
    
    // Faults ---------------------------------------------
    void clearStickyFaults();
    int getFaultForwardLim();
    int getFaultForwardSoftLim();
    int getFaultHardwareFailure();
    int getFaultOverTemp();
    int getFaultReverseLim();
    int getFaultReverseSoftLim();
    int getFaultUnderVoltage();
    int getStickyFaultForwardLim();
    int getStickyFaultForwardSoftLim();
    int getStickyFaultOverTemp();
    int getStickyFaultReverseLim();
    int getStickyFaultReverseSoftLim();
    int getStickyFaultUnderVoltage();
    
    // PID/closed-loop ------------------------------------
    double getP();
    double getI();
    double getD();
    double getF();
    void setP(double p);
    void setI(double i);
    void setD(double d);
    void setF(double f);
    void setPID(double p, double i, double d);
    void clearIAccum();
    int getClosedLoopError();
    void setAllowableClosedLoopError(int allowableError);
    double getIZone();
    void setIZone(int iZone);
    long getIAccum();
    void setClosedLoopRampRate(double rampRate);
    
    // Sensing and input ----------------------------------
    CANTalon.FeedbackDeviceStatus isSensorPresent(CANTalon.FeedbackDevice feedbackDevice);
    void setFeedbackDevice(CANTalon.FeedbackDevice device);
    void configEncoderCodesPerRev(int codesPerRev);
    void configPotentiometerTurns(int turns);
    double getPosition();
    void setPosition(double pos);
    double getSpeed();
    int getAnalogPosition();
    void setAnalogPosition(int newPosition);
    int getAnalogPositionRaw();
    int getAnalogSpeed();
    int getEncoderPosition();
    void setEncoderPosition(int newPosition);
    int getEncoderSpeed();
    void reverseSensor(boolean flip);
    void enableZeroSensorPositionOnIndex(boolean enable, boolean risingEdge);
    int getNumberOfQuadIndexRises();
    
    // Output ---------------------------------------------
    boolean getInverted();
    void setInverted(boolean isInverted);
    void setVoltageCompensationRampRate(double rampRate);
    void configNominalOutputVoltage(double forwardVoltage, double reverseVoltage);
    void configPeakOutputVoltage(double forwardVoltage, double reverseVoltage);
    
    // Soft position limits -------------------------------
    int getForwardSoftLimit();
    int getReverseSoftLimit();
    void setForwardSoftLimit(double forwardLimit);
    void setReverseSoftLimit(double reverseLimit);
    boolean isForwardSoftLimitEnabled();
    boolean isReverseSoftLimitEnabled();
    void enableForwardSoftLimit(boolean enable);
    void enableReverseSoftLimit(boolean enable);
    
    // Limit switches -------------------------------------
    void enableLimitSwitches(boolean forwardEnabled, boolean reverseEnabled);
    boolean isForwardLimitSwitchClosed();
    boolean isReverseLimitSwitchClosed();
    void configForwardLimitSwitchNormallyOpen(boolean normallyOpen);
    void configReverseLimitSwitchNormallyOpen(boolean normallyOpen);
    
    // Setpoints ------------------------------------------
    double get();
    /**
     * Sets the appropriate output on the talon, depending on the mode. In PercentVbus, the output is between -1.0 and
     * 1.0, with 0.0 as stopped. In Follower mode, the output is the integer device ID of the talon to duplicate. In
     * Voltage mode, outputValue is in volts. In Current mode, outputValue is in amperes. In Speed mode, outputValue is
     * in position change / 10ms. In Position mode, outputValue is in encoder ticks or an analog value, depending on the
     * sensor.
     */
    void set(double outputValue);
    
    // Custom helpers -------------------------------------
    void createTelemetryProperties(String deviceName, XPropertyManager propertyManager);
    void updateTelemetryProperties();
}
