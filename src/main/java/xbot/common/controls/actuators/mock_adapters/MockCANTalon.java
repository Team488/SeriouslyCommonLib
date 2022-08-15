package xbot.common.controls.actuators.mock_adapters;

import java.math.BigDecimal;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.InvertType;
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
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.mock_adapters.MockEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.logging.RobotAssertionManager;
import xbot.common.math.MathUtils;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.ISimulatableMotor;
import xbot.common.simulation.ISimulatableSensor;

public class MockCANTalon extends XCANTalon implements ISimulatableSensor, ISimulatableMotor {

    private static Logger log = Logger.getLogger(MockCANTalon.class);

    private int pulseWidthRiseToFallUs = 0;

    public final int deviceId;
    private double setpoint = 0;
    private double throttlePercent = 0;
    public XEncoder internalEncoder = null;
    double current = 0;
    int continuousCurrentLimit = 1000;
    double openLoopRamp = 0;

    private boolean forwardLimitSwitch;
    private boolean reverseLimitSwitch;

    double kp;
    double ki;
    double kd;
    double kf;

    private MockCANTalon master;
    private RobotAssertionManager assertionManager;

    private double simulationScalingValue;

    private boolean inverted;
    private boolean sensorPhaseInverted;

    @AssistedFactory
    public abstract static class MockCANTalonFactory implements XCANTalonFactory {
        public abstract MockCANTalon create(@Assisted("deviceInfo") CANTalonInfo deviceInfo);
    }

    @AssistedInject
    public MockCANTalon(@Assisted("deviceInfo") CANTalonInfo deviceInfo, PropertyFactory propMan, DevicePolice police,
            RobotAssertionManager assertionManager) {
        super(deviceInfo.channel, propMan, police);
        log.info("Creating CAN talon with device ID: " + deviceInfo.channel);

        this.deviceId = deviceInfo.channel;
        this.assertionManager = assertionManager;
        this.simulationScalingValue = deviceInfo.simulationScalingValue;
        double simulationScalingFloor = 0.00001;
        if (Math.abs(simulationScalingValue) < simulationScalingFloor) {
            log.error("Your scaling value was suspiciously low. Are you sure it should be smaller than "
                    + simulationScalingFloor + "?");
        }

        setInverted(deviceInfo.inverted);
        if (deviceInfo.feedbackDevice != null) {
            configSelectedFeedbackSensor(deviceInfo.feedbackDevice, 0, 0);
            setSensorPhase(deviceInfo.feedbackDeviceInverted);
        }
    }

    @Override
    public void set(ControlMode Mode, double demand) {
        set(Mode, demand, 0);
    }

    public double getSetpoint() {
        return setpoint;
    }

    @Override
    public void set(ControlMode Mode, double demand0, double demand1) {

        this.setpoint = demand0 * (this.getInverted() ? -1 : 1);

        switch (Mode) {
        case Disabled:
            throttlePercent = 0;
            break;
        case PercentOutput:
            throttlePercent = setpoint;
            throttlePercent = MathUtils.constrainDoubleToRobotScale(throttlePercent);
            break;
        case Current:
            // Guess voltage by assuming a linear relationship between current and voltage,
            // bypassing PID
            throttlePercent = setpoint / 40.0;
            break;
        case MotionProfile:
            // This mode isn't supported in by the mock implementation (nor the real one)
            throttlePercent = 0;
            break;
        case Velocity:
            double rate = internalEncoder.getAdjustedRate();
            if (setpoint > rate) {
                throttlePercent = 1;
            }
            if (setpoint < rate) {
                throttlePercent = -1;
            }
            break;
        case Position:
            if (!Double.isFinite(this.kp)) {
                throttlePercent = 0;
            } else {
                // Highly efficient P(IDF) implementation
                throttlePercent = (this.setpoint - getPosition()) * this.kp;
            }
            break;
        case Follower:
            throttlePercent = master.getMotorOutputPercent();
            break;
        default:
            throttlePercent = 0;
        }
    }

    /**
     * Returns the low-level throttle percentage that would typically be sent to a motor.
     * For example, if a motor is configured as Inverted, and it was set to a value of 0.75:
     * getThrottlePercent (this method) would return -0.75
     * getMotorOutputPercent would return 0.75
     * As such, this is the method you would want to hook into consumers like a simulator, so that
     * changing inversion would affect them.
     * @return Motor throttle percentage (potentially inverted)
     */
    public double getThrottlePercent() {
        if (master == null) {
            return this.throttlePercent;
        } else {
            return this.master.getThrottlePercent();
        }
    }

    @Override
    public void neutralOutput() {

    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {

    }

    @Override
    public void setSensorPhase(boolean PhaseSensor) {
        this.sensorPhaseInverted = PhaseSensor;
    }

    @Override
    public void setInverted(boolean invert) {
        this.inverted = invert;
    }

    @Override
    public boolean getInverted() {
        return this.inverted;
    }

    @Override
    public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
        openLoopRamp = secondsFromNeutralToFull;
        return null;
    }

    public double getOpenLoopRamp() {
        return openLoopRamp;
    }

    @Override
    public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {

        return null;
    }

    @Override
    public void enableVoltageCompensation(boolean enable) {

    }

    @Override
    public double getBusVoltage() {

        return 12;
    }

    @Override
    public double getMotorOutputPercent() {
        // if the Talon is set to invert, it will output negative voltages. This needs
        // to be taken into account.

        double inversionFactor = this.getInverted() ? -1 : 1;
        return this.getThrottlePercent() * inversionFactor;
    }

    @Override
    public double getMotorOutputVoltage() {

        return getMotorOutputPercent() * this.getBusVoltage();
    }

    @Override
    public double getOutputCurrent() {

        return current;
    }

    public void setOutputCurrent(double current) {
        this.current = current;
    }

    @Override
    public double getTemperature() {

        return 0;
    }

    @Override
    public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal,
            int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
        if (feedbackDevice == FeedbackDevice.QuadEncoder || feedbackDevice == FeedbackDevice.CTRE_MagEncoder_Relative
                || feedbackDevice == FeedbackDevice.CTRE_MagEncoder_Absolute) {
            this.internalEncoder = new MockEncoder("Test", propMan);
        } else {
            assertionManager.fail(
                    "Whatever you supplied is not supported by the test infrastructure! Update MockCANTalon to handle your scenario.");
        }

        return ErrorCode.OK;
    }

    @Override
    public double getSelectedSensorPosition(int pidIdx) {

        return (int) getPosition() * (sensorPhaseInverted ? -1 : 1);
    }

    @Override
    public double getSelectedSensorVelocity(int pidIdx) {

        return 0;
    }

    @Override
    public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {

        return null;
    }

    @Override
    public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {

        return null;
    }

    @Override
    public int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {

        return 0;
    }

    @Override
    public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {

        return 0;
    }

    @Override
    public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs) {

        return null;
    }

    @Override
    public void overrideLimitSwitchesEnable(boolean enable) {

    }

    @Override
    public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {

        return null;
    }

    @Override
    public void overrideSoftLimitsEnable(boolean enable) {

    }

    public boolean isFwdLimitSwitchClosed() {
        return forwardLimitSwitch;
    }

    public void setForwardLimitSwitch(boolean value) {
        forwardLimitSwitch = value;
    }

    public boolean isRevLimitSwitchClosed() {
        return reverseLimitSwitch;
    }

    public void setReverseLimitSwitch(boolean value) {
        reverseLimitSwitch = value;
    }

    @Override
    public ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
        continuousCurrentLimit = amps;
        return null;
    }

    public int getContinuousCurrentLimit() {
        return continuousCurrentLimit;
    }

    @Override
    public void enableCurrentLimit(boolean enable) {

    }

    @Override
    public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
        this.kp = value;
        return null;
    }

    @Override
    public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode config_IntegralZone(int slotIdx, int izone, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableCloseLoopError, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {

        return null;
    }

    @Override
    public double getClosedLoopError(int pidIdx) {

        return 0;
    }

    @Override
    public double getIntegralAccumulator(int pidIdx) {

        return 0;
    }

    @Override
    public double getErrorDerivative(int pidIdx) {

        return 0;
    }

    @Override
    public void selectProfileSlot(int slotIdx, int pidIdx) {

    }

    @Override
    public double getActiveTrajectoryPosition() {

        return 0;
    }

    @Override
    public double getActiveTrajectoryVelocity() {

        return 0;
    }

    @Override
    public double getActiveTrajectoryHeading() {

        return 0;
    }

    @Override
    public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode clearMotionProfileTrajectories() {

        return null;
    }

    @Override
    public int getMotionProfileTopLevelBufferCount() {

        return 0;
    }

    @Override
    public ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {

        return null;
    }

    @Override
    public boolean isMotionProfileTopLevelBufferFull() {

        return false;
    }

    @Override
    public void processMotionProfileBuffer() {

    }

    @Override
    public ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {

        return null;
    }

    @Override
    public ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode changeMotionControlFramePeriod(int periodMs) {

        return null;
    }

    @Override
    public ErrorCode getLastError() {

        return null;
    }

    @Override
    public ErrorCode getFaults(Faults toFill) {

        return null;
    }

    @Override
    public ErrorCode getStickyFaults(StickyFaults toFill) {

        return null;
    }

    @Override
    public ErrorCode clearStickyFaults(int timeoutMs) {

        return null;
    }

    @Override
    public int getFirmwareVersion() {

        return 0;
    }

    @Override
    public boolean hasResetOccurred() {

        return false;
    }

    @Override
    public ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {

        return null;
    }

    @Override
    public int configGetCustomParam(int paramIndex, int timoutMs) {

        return 0;
    }

    @Override
    public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {

        return null;
    }

    @Override
    public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs) {

        return null;
    }

    @Override
    public double configGetParameter(ParamEnum paramEnum, int ordinal, int timeoutMs) {

        return 0;
    }

    @Override
    public double configGetParameter(int paramEnum, int ordinal, int timeoutMs) {

        return 0;
    }

    @Override
    public int getBaseID() {

        return 0;
    }

    @Override
    public int getDeviceID() {

        return 0;
    }

    @Override
    public void follow(IMotorController masterToFollow) {
        master = (MockCANTalon) masterToFollow;
    }

    @Override
    public void valueUpdated() {

    }

    public double getPosition() {
        if (internalEncoder == null) {
            assertionManager.fail("Position requested before setting feedback device!");
            return 0;
        }

        return internalEncoder.getAdjustedDistance();
    }

    public void setPosition(double pos) {
        if (internalEncoder == null) {
            assertionManager.fail("Position set before setting feedback device!");
        } else {
            ((MockEncoder) internalEncoder).setDistance(pos);
        }
    }

    public void setRate(double rate) {
        if (internalEncoder == null) {
            assertionManager.fail("Rate set before setting feedback device!");
        } else {
            ((MockEncoder) internalEncoder).setRate(rate);
        }
    }

    @Override
    public void set(ControlMode Mode, double demand0, DemandType demand1Type, double demand1) {

    }

    @Override
    public ErrorCode configSelectedFeedbackCoefficient(double coefficient, int pidIdx, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configAuxPIDPolarity(boolean invert, int timeoutMs) {
        return null;
    }

    @Override
    public double getClosedLoopTarget(int pidIdx) {
        return 0;
    }

    @Override
    public ErrorCode configMotionProfileTrajectoryPeriod(int baseTrajDurationMs, int timeoutMs) {
        return null;
    }

    @Override
    public ControlMode getControlMode() {
        return null;
    }

    @Override
    public ErrorCode configMotionSCurveStrength(int curveStrength, int timeoutMs) {
        return null;
    }

    @Override
    public int getPulseWidthRiseToFallUs() {
        return pulseWidthRiseToFallUs;
    }

    public void setPulseWidthRiseToFallUs(int value) {
        pulseWidthRiseToFallUs = value;
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        BigDecimal intermediate = (BigDecimal) payload.get("EncoderTicks");
        setPosition((int) (intermediate.doubleValue() * simulationScalingValue));
    }

    @Override
    public JSONObject getSimulationData() {
        return buildMotorObject(policeTicket, (float)getThrottlePercent());
    }

    @Override
    public ErrorCode configSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitCfg, int timeoutMs) {
        return null;
    }

    @Override
    public void setInverted(InvertType invertType) {
        
    }

    @Override
    public ErrorCode configRemoteFeedbackFilter(CANCoder canCoderRef, int remoteOrdinal, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configRemoteFeedbackFilter(BaseTalon talonRef, int remoteOrdinal, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode setSelectedSensorPosition(double sensorPos, int pidIdx, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configForwardSoftLimitThreshold(double forwardSensorLimit, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configReverseSoftLimitThreshold(double reverseSensorLimit, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode config_IntegralZone(int slotIdx, double izone, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configAllowableClosedloopError(int slotIdx, double allowableCloseLoopError, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configMotionCruiseVelocity(double sensorUnitsPer100ms, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configMotionAcceleration(double sensorUnitsPer100msPerSec, int timeoutMs) {
        return null;
    }

    @Override
    public ErrorCode configVelocityMeasurementPeriod(SensorVelocityMeasPeriod period, int timeoutMs) {
        return null;
    }

    @Override
    @SuppressWarnings( "deprecation" )
    public ErrorCode configVelocityMeasurementPeriod(com.ctre.phoenix.motorcontrol.VelocityMeasPeriod period, int timeoutMs) {
        return null;
    }
}
