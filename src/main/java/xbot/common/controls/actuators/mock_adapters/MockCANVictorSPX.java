package xbot.common.controls.actuators.mock_adapters;

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
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.actuators.XCANVictorSPX;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class MockCANVictorSPX extends XCANVictorSPX {

    double power;

    @Inject
    public MockCANVictorSPX(@Assisted("deviceId") int deviceId, PropertyFactory propMan, DevicePolice police) {
        super(deviceId, propMan, police);
    }

    //CHECKSTYLE:OFF

    @Override
    public void set(ControlMode Mode, double demand0, DemandType demand1Type, double demand1) {
        power = demand0;
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
    public ErrorCode configMotionSCurveStrength(int curveStrength, int timeoutMs) {
        return null;
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
    public void set(ControlMode Mode, double demand) {

    }

    @Override
    public void neutralOutput() {

    }

    @Override
    public void setNeutralMode(NeutralMode neutralMode) {

    }

    @Override
    public void setSensorPhase(boolean PhaseSensor) {

    }

    @Override
    public void setInverted(boolean invert) {

    }

    @Override
    public boolean getInverted() {
        return false;
    }

    @Override
    public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
        return null;
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
        return 0;
    }

    @Override
    public double getMotorOutputPercent() {
        return power;
    }

    @Override
    public double getMotorOutputVoltage() {
        return 0;
    }

    @Override
    public double getOutputCurrent() {
        return 0;
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
        return null;
    }

    @Override
    public int getSelectedSensorPosition(int pidIdx) {
        return 0;
    }

    @Override
    public int getSelectedSensorVelocity(int pidIdx) {
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
    public ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs) {
        return null;
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
    public boolean isFwdLimitSwitchClosed() {
        return false;
    }

    @Override
    public boolean isRevLimitSwitchClosed() {
        return false;
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
        return null;
    }

    @Override
    public void enableCurrentLimit(boolean enable) {

    }

    @Override
    public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
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
    public int getClosedLoopError(int pidIdx) {
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
    public int getActiveTrajectoryPosition() {
        return 0;
    }

    @Override
    public int getActiveTrajectoryVelocity() {
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

    }

    @Override
    public void valueUpdated() {

    }

    @Override
    public int getPulseWidthRiseToFallUs() {
        return 0;
    }

    //CHECKSTYLE:ON
}