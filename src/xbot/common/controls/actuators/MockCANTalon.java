package xbot.common.controls.actuators;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.FeedbackDeviceStatus;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.SpeedController;
import xbot.common.controls.MockRobotIO;
import xbot.common.controls.sensors.MockEncoder;
import xbot.common.properties.XPropertyManager;

public class MockCANTalon implements XCANTalon {    
    public final int deviceId;
    private CANTalon.TalonControlMode controlMode;
    private CANTalon.TalonControlMode lastSetControlMode;
    
    private boolean outputInverted = false;
    
    private double setpoint = 0;
    private double throttlePercent = 0;
    
    private int currentProfile = 0;
    private Map<Integer, ProfileParams> profiles = new HashMap<>();
    
    private static class ProfileParams
    {
        public double p;
        public double i;
        public double d;
        public double f;
        public double iZone;
    }
    
    private static final ProfileParams defaultParams = new ProfileParams();
    
    MockRobotIO mockRobotIO;
    public MockEncoder internalEncoder = null;

    private static Logger log = Logger.getLogger(MockCANTalon.class);

    public MockCANTalon(int deviceId, MockRobotIO mockRobotIO) {
        log.info("Creating CAN talon with device ID: " + deviceId);
        
        this.deviceId = deviceId;
        this.mockRobotIO = mockRobotIO;
        mockRobotIO.setCANTalon(deviceId, this);
        
        this.setControlMode(TalonControlMode.Disabled);
    }
    
    private void initCurrentProfileParams() {
        if(!this.profiles.containsKey(currentProfile)) {
            this.profiles.put(currentProfile, new ProfileParams());
        }
    }
    
    /**
     * Getter for throttle as percentage. Used for follower mode.
     */
    public double getThrottlePercent() {
        return this.throttlePercent;
    }
    
    @Override
    public SpeedController getInternalController() {
        return null;
    }

    @Override
    public int getChannel() {
        return this.getDeviceID();
    }

    @Override
    public boolean isEnabled() {
        return controlMode != CANTalon.TalonControlMode.Disabled;
    }

    @Override
    public void enable() {
        controlMode = lastSetControlMode;
    }

    @Override
    public void disable() {
        controlMode = CANTalon.TalonControlMode.Disabled;
    }

    @Override
    public void setProfile(int profile) {
        this.currentProfile = profile;
    }

    @Override
    public TalonControlMode getControlMode() {
        return controlMode;
    }

    @Override
    public void setControlMode(TalonControlMode controlMode) {
        this.controlMode = controlMode;
        this.lastSetControlMode = controlMode;
    }

    @Override
    public boolean getBrakeEnableDuringNeutral() {
        // Brake isn't supported in mock environment.
        return false;
    }

    @Override
    public void setBrakeEnableDuringNeutral(boolean brake) {
        // Intentionally left blank. Brake isn't supported in mock environment.
    }

    @Override
    public void setStatusFrameRateMs(StatusFrameRate stateFrame, int periodMs) {
        // Intentionally left blank. There isn't an update rate in the mock environment.
    }

    @Override
    public void reset() {
        this.clearIAccum();
        this.disable();
    }

    @Override
    public int getDeviceID() {
        return this.deviceId;
    }

    @Override
    public double getOutputCurrent() {
        if(controlMode == TalonControlMode.Follower) {
            return mockRobotIO.getCANTalon((int)setpoint).getOutputCurrent();
        }
        
        return Math.abs(setpoint) > 0.01 ? MockRobotIO.NOMINAL_MOTOR_CURRENT : 0;
    }

    @Override
    public double getOutputVoltage() {
        return this.getThrottlePercent() * this.getBusVoltage();
    }

    @Override
    public double getTemperature() {
        // No point in mocking this
        return 0;
    }

    @Override
    public double getBusVoltage() {
        return MockRobotIO.BUS_VOLTAGE;
    }

    @Override
    public long getFirmwareVersion() {
        return 0;
    }

    @Override
    public void clearStickyFaults() {
        // Intentionally left blank
    }

    @Override
    public int getFaultForwardLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getFaultForwardSoftLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getFaultHardwareFailure() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getFaultOverTemp() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getFaultReverseLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getFaultReverseSoftLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getFaultUnderVoltage() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getStickyFaultForwardLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getStickyFaultForwardSoftLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getStickyFaultOverTemp() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getStickyFaultReverseLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getStickyFaultReverseSoftLim() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public int getStickyFaultUnderVoltage() {
        // Intentionally left blank
        return 0;
    }

    @Override
    public double getP() {
        return this.profiles.getOrDefault(currentProfile, defaultParams).p;
    }

    @Override
    public double getI() {
        return this.profiles.getOrDefault(currentProfile, defaultParams).i;
    }

    @Override
    public double getD() {
        return this.profiles.getOrDefault(currentProfile, defaultParams).d;
    }

    @Override
    public double getF() {
        return this.profiles.getOrDefault(currentProfile, defaultParams).f;
    }

    @Override
    public void setP(double p) {
        initCurrentProfileParams();
        this.profiles.get(currentProfile).p = p;
    }

    @Override
    public void setI(double i) {
        initCurrentProfileParams();
        this.profiles.get(currentProfile).i = i;
    }

    @Override
    public void setD(double d) {
        initCurrentProfileParams();
        this.profiles.get(currentProfile).d = d;
    }

    @Override
    public void setF(double f) {
        initCurrentProfileParams();
        this.profiles.get(currentProfile).f = f;

    }

    @Override
    public void setPID(double p, double i, double d) {
        this.setP(p);
        this.setI(i);
        this.setD(d);
    }

    @Override
    public void clearIAccum() {
        // Intentionally left blank. The mock implementation does not use full PID.
    }

    @Override
    public int getClosedLoopError() {
        if(internalEncoder == null) {
            return 0;
        }
        
        double currentPos;
        if(controlMode == TalonControlMode.Position) {
            currentPos = internalEncoder.getDistance();
        }
        else if (controlMode == TalonControlMode.Speed) {
            currentPos = internalEncoder.getRate();
        }
        else {
            return 0;
        }
        
        return (int)(setpoint - currentPos);
    }

    @Override
    public void setAllowableClosedLoopError(int allowableError) {
        // Intentionally left blank. No point in doing the extra math in the mock impl.
    }

    @Override
    public double getIZone() {
        // There isn't a full PID implementation here, so we don't need windup prevention
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public void setIZone(int iZone) {
        // Intentionally left blank.
    }

    @Override
    public long getIAccum() {
        // No full PID impl. in mock
        return 0;
    }

    @Override
    public void setClosedLoopRampRate(double rampRate) {
        // Intentionally left blank. The mock implementation does not handle ramping.
    }

    @Override
    public FeedbackDeviceStatus isSensorPresent(FeedbackDevice feedbackDevice) {
        // The mock implementation only supports quadrature encoders
        return feedbackDevice == FeedbackDevice.QuadEncoder ? FeedbackDeviceStatus.FeedbackStatusPresent : FeedbackDeviceStatus.FeedbackStatusNotPresent;
    }

    @Override
    public void setFeedbackDevice(FeedbackDevice device) {
        if(device == FeedbackDevice.QuadEncoder) {
            this.internalEncoder = new MockEncoder();
        }
    }

    @Override
    public void configEncoderCodesPerRev(int codesPerRev) {
        // This value isn't used and there is no getter that needs it, so we can ignore it.
    }

    @Override
    public void configPotentiometerTurns(int turns) {
        // Intentionally left blank. The mock implementation only supports quadrature encoders.
    }

    @Override
    public double getPosition() {
        if(internalEncoder == null) {
            log.warn("Position requested before setting feedback device!");
            return 0;
        }
        
        return this.getEncoderPosition();
    }

    @Override
    public void setPosition(double pos) {
        if(internalEncoder == null) {
            log.warn("Position set before setting feedback device!");
        }
        else {
            internalEncoder.setDistance(pos);
        }
    }

    @Override
    public double getSpeed() {
        if(internalEncoder == null) {
            log.warn("Speed requested before setting feedback device!");
            return 0;
        }
        
        return this.getEncoderSpeed();
    }

    @Override
    public int getAnalogPosition() {
        // Analog feedback sensors aren't supported by mock impl.
        return 0;
    }

    @Override
    public void setAnalogPosition(int newPosition) {
        // Intentionally left blank. Analog feedback sensors aren't supported by mock impl.
    }

    @Override
    public int getAnalogPositionRaw() {
     // Analog feedback sensors aren't supported by mock impl.
        return 0;
    }

    @Override
    public int getAnalogSpeed() {
     // Analog feedback sensors aren't supported by mock impl.
        return 0;
    }

    @Override
    public int getEncoderPosition() {
        return internalEncoder == null ? 0 : (int) internalEncoder.getDistance();
    }

    @Override
    public void setEncoderPosition(int newPosition) {
        if(internalEncoder == null) {
            // Because the nullity of internalEncoder is used to determine whether a
            //  feedback device has been chosen, initializing it here will have the
            //  unintended side-effect of no longer warning about an unset sensor.
            internalEncoder = new MockEncoder();
        }
        
        internalEncoder.setDistance(newPosition);
    }

    @Override
    public int getEncoderSpeed() {
        return internalEncoder == null ? 0 : (int) internalEncoder.getRate();
    }

    @Override
    public void reverseSensor(boolean flip) {
        if(internalEncoder != null) {
            internalEncoder.setInverted(flip);
        }
    }

    @Override
    public void enableZeroSensorPositionOnIndex(boolean enable, boolean risingEdge) {
        // Intentionally left blank. Mock does not implement index pin.
    }

    @Override
    public int getNumberOfQuadIndexRises() {
        // Mock does not implement index pin.
        return 0;
    }

    @Override
    public boolean getInverted() {
        return outputInverted;
    }

    @Override
    public void setInverted(boolean isInverted) {
        this.outputInverted = isInverted;
    }

    @Override
    public void setVoltageCompensationRampRate(double rampRate) {
        // Intentionally left blank. Mock impl. doesn't support ramping.
    }

    @Override
    public void configNominalOutputVoltage(double forwardVoltage, double reverseVoltage) {
        // Intentionally left blank. Min output voltages aren't necessary in mock environment.
    }

    @Override
    public void configPeakOutputVoltage(double forwardVoltage, double reverseVoltage) {
        // Intentionally left blank. Max output voltages aren't necessary in mock environment.
    }

    @Override
    public int getForwardSoftLimit() {
        // Soft limits aren't implemented in mock environment.
        return Integer.MAX_VALUE;
    }

    @Override
    public int getReverseSoftLimit() {
        // Soft limits aren't implemented in mock environment.
        return Integer.MIN_VALUE;
    }

    @Override
    public void setForwardSoftLimit(double forwardLimit) {
        // Intentionally left blank. Soft limits aren't implemented in mock environment.
    }

    @Override
    public void setReverseSoftLimit(double reverseLimit) {
        // Intentionally left blank. Soft limits aren't implemented in mock environment.
    }

    @Override
    public boolean isForwardSoftLimitEnabled() {
        // Soft limits aren't implemented in mock environment.
        return false;
    }

    @Override
    public boolean isReverseSoftLimitEnabled() {
        // Soft limits aren't implemented in mock environment.
        return false;
    }

    @Override
    public void enableForwardSoftLimit(boolean enable) {
        // Intentionally left blank. Soft limits aren't implemented in mock environment.
    }

    @Override
    public void enableReverseSoftLimit(boolean enable) {
        // Intentionally left blank. Soft limits aren't implemented in mock environment.
    }

    @Override
    public void enableLimitSwitches(boolean forwardEnabled, boolean reverseEnabled) {
        // Intentionally left blank. Limit switches aren't implemented in mock environment.
    }

    @Override
    public boolean isForwardLimitSwitchClosed() {
        // Limit switches aren't implemented in mock environment.
        return false;
    }

    @Override
    public boolean isReverseLimitSwitchClosed() {
        // Limit switches aren't implemented in mock environment.
        return false;
    }

    @Override
    public void configForwardLimitSwitchNormallyOpen(boolean normallyOpen) {
        // Intentionally left blank. Limit switches aren't implemented in mock environment.
    }

    @Override
    public void configReverseLimitSwitchNormallyOpen(boolean normallyOpen) {
        // Intentionally left blank. Limit switches aren't implemented in mock environment.
    }

    @Override
    public double get() {
        switch(controlMode) {
            case Disabled:
                return 0;
            case Voltage:
                return getOutputVoltage();
            case PercentVbus:
                return setpoint;
            case Current:
                return this.getOutputCurrent();
            case MotionProfile:
                // This mode isn't supported in by the mock implementation (nor the real one)
                return 0;
            case Speed:
                return this.getSpeed();
            case Position:
                return this.getPosition();
            case Follower:
                MockCANTalon master = mockRobotIO.getCANTalon((int)setpoint);
                return master.getOutputVoltage() / master.getBusVoltage();
            default:
                return 0;
        }
    }
    
    /**
     * Test-only - get the most resent set() value.
     * @return
     */
    public double getSetpoint() {
        return setpoint;
    }

    @Override
    public void set(double outputValue) {
        this.setpoint = outputValue;
        
        switch(controlMode) {
            case Disabled:
                throttlePercent = 0;
                break;
            case Voltage:
                throttlePercent = setpoint / this.getBusVoltage();
                break;
            case PercentVbus:
                throttlePercent = setpoint;
                break;
            case Current:
                // Guess voltage by assuming a linear relationship between current and voltage, bypassing PID
                throttlePercent = setpoint / MockRobotIO.NOMINAL_MOTOR_CURRENT;
                break;
            case MotionProfile:
                // This mode isn't supported in by the mock implementation (nor the real one)
                throttlePercent = 0;
                break;
            case Speed:
            case Position:
                if(!Double.isFinite(this.getP())) {
                    throttlePercent = 0;
                }
                else {
                    // Highly efficient P(IDF) implementation
                    throttlePercent = this.getClosedLoopError() * this.getP();
                }
                break;
            case Follower:
                throttlePercent = mockRobotIO.getCANTalon((int)setpoint).getThrottlePercent();
                break;
            default:
                throttlePercent = 0;
        }
        
        mockRobotIO.setPWM(-deviceId, this.getOutputVoltage() / this.getBusVoltage());
    }

    @Override
    public void createTelemetryProperties(String deviceName, XPropertyManager propertyManager) {
        // Intentionally left blank. There is no need for properties in mock mode.
    }

    @Override
    public void updateTelemetryProperties() {
        // Intentionally left blank. There is no need for properties in mock mode.
    }

    /**
     * When working with the real implementation of the talon, we want to minimize
     * setControlMode calls, as these appear to send a message across the CAN bus, and
     * the bus has a finite bandwidth.
     * 
     * However, the Mock implementation has no such restrictions (it's all in-memory faked
     * stuff), so we can just call set every tick.
     */
    @Override
    public void ensureTalonControlMode(TalonControlMode mode) {
        this.setControlMode(mode);
    }

}
