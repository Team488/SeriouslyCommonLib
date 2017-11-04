package xbot.common.controls.actuators;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.StringProperty;
import xbot.common.properties.XPropertyManager;

import java.util.concurrent.TimeUnit;

import org.influxdb.dto.Point;

public abstract class XCANTalon {
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
    
    public XCANTalon(int deviceId, XPropertyManager propMan) {
        this.deviceId = deviceId;
        this.propMan = propMan;
    }
    
    public void createTelemetryProperties(String deviceName) {
        controlModeProperty = propMan.createEphemeralProperty(deviceName + " control mode", CANTalon.TalonControlMode.Disabled.name());
        currentProperty = propMan.createEphemeralProperty(deviceName + " current", 0);
        outVoltageProperty = propMan.createEphemeralProperty(deviceName + " voltage", 0);
        temperatureProperty = propMan.createEphemeralProperty(deviceName + " temperature", 0);
    }
    
    public void updateTelemetryProperties() {
        if(controlModeProperty == null
                || currentProperty == null
                || outVoltageProperty == null
                || temperatureProperty == null) {
            return;
        }
        
        controlModeProperty.set(this.getControlMode().name());
        currentProperty.set(this.getOutputCurrent());
        outVoltageProperty.set(this.getOutputVoltage());
        temperatureProperty.set(this.getTemperature());
    }

    /**
     * When working with the real implementation of the talon, we want to minimize
     * setControlMode calls, as these appear to send a message across the CAN bus, and
     * the bus has a finite bandwidth. However, the call to getControlMode appears to read
     * from local in-memory information about the Talon, and thus is much cheaper to call
     * repeatedly.
     */
    public void ensureTalonControlMode(TalonControlMode mode) {
        if (this.getControlMode() != mode) {
            this.setControlMode(mode);
        }
    }
    
    // Control and meta config ----------------------------
    public abstract boolean isEnabled();
    public abstract void enable();
    public abstract void disable();
    public abstract void setProfile(int profile);
    public abstract CANTalon.TalonControlMode getControlMode();
    public abstract void setControlMode(CANTalon.TalonControlMode controlMode);
    public abstract boolean getBrakeEnableDuringNeutral();
    public abstract void setBrakeEnableDuringNeutral(boolean brake);
    public abstract void setStatusFrameRateMs(CANTalon.StatusFrameRate stateFrame, int periodMs);
    public abstract void reset();
    
    // Meta information -----------------------------------
    public abstract int getDeviceID();
    public abstract double getOutputCurrent();
    public abstract double getOutputVoltage();
    public abstract double getTemperature();
    public abstract double getBusVoltage();
    public abstract long getFirmwareVersion();
    
    // Faults ---------------------------------------------
    public abstract void clearStickyFaults();
    public abstract int getFaultForwardLim();
    public abstract int getFaultForwardSoftLim();
    public abstract int getFaultHardwareFailure();
    public abstract int getFaultOverTemp();
    public abstract int getFaultReverseLim();
    public abstract int getFaultReverseSoftLim();
    public abstract int getFaultUnderVoltage();
    public abstract int getStickyFaultForwardLim();
    public abstract int getStickyFaultForwardSoftLim();
    public abstract int getStickyFaultOverTemp();
    public abstract int getStickyFaultReverseLim();
    public abstract int getStickyFaultReverseSoftLim();
    public abstract int getStickyFaultUnderVoltage();
    
    // PID/closed-loop ------------------------------------
    public abstract double getP();
    public abstract double getI();
    public abstract double getD();
    public abstract double getF();
    public abstract void setP(double p);
    public abstract void setI(double i);
    public abstract void setD(double d);
    public abstract void setF(double f);
    public abstract void setPID(double p, double i, double d);
    public abstract void clearIAccum();
    public abstract int getClosedLoopError();
    public abstract void setAllowableClosedLoopError(int allowableError);
    public abstract double getIZone();
    public abstract void setIZone(int iZone);
    public abstract long getIAccum();
    public abstract void setClosedLoopRampRate(double rampRate);
    
    
    // Sensing and input ----------------------------------
    public abstract CANTalon.FeedbackDeviceStatus isSensorPresent(CANTalon.FeedbackDevice feedbackDevice);
    public abstract void setFeedbackDevice(CANTalon.FeedbackDevice device);
    public abstract void configEncoderCodesPerRev(int codesPerRev);
    public abstract void configPotentiometerTurns(int turns);
    public abstract double getPosition();
    public abstract void setPosition(double pos);
    public abstract double getSpeed();
    public abstract int getAnalogPosition();
    public abstract void setAnalogPosition(int newPosition);
    public abstract int getAnalogPositionRaw();
    public abstract int getAnalogSpeed();
    public abstract int getEncoderPosition();
    public abstract void setEncoderPosition(int newPosition);
    public abstract int getEncoderSpeed();
    public abstract void reverseSensor(boolean flip);
    public abstract void enableZeroSensorPositionOnIndex(boolean enable, boolean risingEdge);
    public abstract int getNumberOfQuadIndexRises();
    
    // Output ---------------------------------------------
    boolean getInverted();
    
    /**
     * This inverts the motor for operations like PercentVBus, but DOES NOT INVERT
     * THE MOTOR FOR ClOSED-LOOP CONTROL OR FOLLOWER CONTROL!!! For that, use reverseOutput().
     * @param isInverted
     */
    void setInverted(boolean isInverted);
    
    /**
     * This is used to reverse the closed-loop output of a CANTalon. In addition, this will 
     * also cause a follower motor to move in the opposite direction of the master motor.
     * @param isInverted
     */
    void reverseOutput(boolean isInverted);
    
    void setVoltageCompensationRampRate(double rampRate);
    void configNominalOutputVoltage(double forwardVoltage, double reverseVoltage);
    void configPeakOutputVoltage(double forwardVoltage, double reverseVoltage);
        
    // Soft position limits -------------------------------
    public abstract int getForwardSoftLimit();
    public abstract int getReverseSoftLimit();
    public abstract void setForwardSoftLimit(double forwardLimit);
    public abstract void setReverseSoftLimit(double reverseLimit);
    public abstract boolean isForwardSoftLimitEnabled();
    public abstract boolean isReverseSoftLimitEnabled();
    public abstract void enableForwardSoftLimit(boolean enable);
    public abstract void enableReverseSoftLimit(boolean enable);
    
    // Limit switches -------------------------------------
    public abstract void enableLimitSwitches(boolean forwardEnabled, boolean reverseEnabled);
    public abstract boolean isForwardLimitSwitchClosed();
    public abstract boolean isReverseLimitSwitchClosed();
    public abstract void configForwardLimitSwitchNormallyOpen(boolean normallyOpen);
    public abstract void configReverseLimitSwitchNormallyOpen(boolean normallyOpen);
    
    // Setpoints ------------------------------------------
    public abstract double get();
    /**
     * Sets the appropriate output on the talon, depending on the mode. In PercentVbus, the output is between -1.0 and
     * 1.0, with 0.0 as stopped. In Follower mode, the output is the integer device ID of the talon to duplicate. In
     * Voltage mode, outputValue is in volts. In Current mode, outputValue is in amperes. In Speed mode, outputValue is
     * in position change / 10ms. In Position mode, outputValue is in encoder ticks or an analog value, depending on the
     * sensor.
     */
    public abstract void set(double outputValue);

    // LiveWindow
    public abstract LiveWindowSendable getLiveWindowSendable();
        
    public Point getTelemetryPoint(String className, String side, boolean addDistance) {
        Point.Builder telemetryPoints = Point.measurement(className)
            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
            .tag("side", side)
            .addField("power", get())
            .addField("current", getOutputCurrent());
        if (addDistance) {
            telemetryPoints.addField("distance", getPosition());
        } 
        return telemetryPoints.build();       
    }
}
