package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motion.BufferedTrajectoryPointStream;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.FollowerType;
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
import com.ctre.phoenix.motorcontrol.can.FilterConfiguration;
import com.ctre.phoenix.motorcontrol.can.SlotConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;
import com.ctre.phoenix.motorcontrol.can.VictorSPXPIDSetConfiguration;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XCANVictorSPX;
import xbot.common.injection.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class CANVictorSPXWpiAdapter extends XCANVictorSPX {

    final VictorSPX internalVictor;

    @AssistedFactory
    public abstract static class CANVictorSPXWpiAdapterFactory implements XCANVictorSPXFactory {
        public abstract CANVictorSPXWpiAdapter create(@Assisted("deviceId") int deviceId);
    }

    @AssistedInject
    public CANVictorSPXWpiAdapter(@Assisted("deviceId") int deviceId, PropertyFactory propMan, DevicePolice police) {
        super(deviceId, propMan, police);
        internalVictor = new VictorSPX(deviceId);
    }

    //CHECKSTYLE:OFF

    public ErrorCode DestroyObject() {
        return internalVictor.DestroyObject();
    }

    public boolean equals(Object obj) {
        return internalVictor.equals(obj);
    }

    public long getHandle() {
        return internalVictor.getHandle();
    }

    public int getDeviceID() {
        return internalVictor.getDeviceID();
    }

    public void getPIDConfigs(VictorSPXPIDSetConfiguration pid, int pidIdx, int timeoutMs) {
        internalVictor.getPIDConfigs(pid, pidIdx, timeoutMs);
    }

    public int hashCode() {
        return internalVictor.hashCode();
    }

    public void set(ControlMode mode, double outputValue) {
        internalVictor.set(mode, outputValue);
    }

    public void getPIDConfigs(VictorSPXPIDSetConfiguration pid) {
        internalVictor.getPIDConfigs(pid);
    }

    public ErrorCode configAllSettings(VictorSPXConfiguration allConfigs, int timeoutMs) {
        return internalVictor.configAllSettings(allConfigs, timeoutMs);
    }

    public void set(ControlMode mode, double demand0, DemandType demand1Type, double demand1) {
        internalVictor.set(mode, demand0, demand1Type, demand1);
    }

    public ErrorCode configAllSettings(VictorSPXConfiguration allConfigs) {
        return internalVictor.configAllSettings(allConfigs);
    }

    public void getAllConfigs(VictorSPXConfiguration allConfigs, int timeoutMs) {
        internalVictor.getAllConfigs(allConfigs, timeoutMs);
    }

    public void neutralOutput() {
        internalVictor.neutralOutput();
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        internalVictor.setNeutralMode(neutralMode);
    }

    public void getAllConfigs(VictorSPXConfiguration allConfigs) {
        internalVictor.getAllConfigs(allConfigs);
    }

    public void setSensorPhase(boolean PhaseSensor) {
        internalVictor.setSensorPhase(PhaseSensor);
    }

    public void setInverted(boolean invert) {
        internalVictor.setInverted(invert);
    }

    public void setInverted(InvertType invertType) {
        internalVictor.setInverted(invertType);
    }

    public boolean getInverted() {
        return internalVictor.getInverted();
    }

    public ErrorCode configFactoryDefault(int timeoutMs) {
        return internalVictor.configFactoryDefault(timeoutMs);
    }

    public ErrorCode configFactoryDefault() {
        return internalVictor.configFactoryDefault();
    }

    public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
        return internalVictor.configOpenloopRamp(secondsFromNeutralToFull, timeoutMs);
    }

    public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull) {
        return internalVictor.configOpenloopRamp(secondsFromNeutralToFull);
    }

    public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
        return internalVictor.configClosedloopRamp(secondsFromNeutralToFull, timeoutMs);
    }

    public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull) {
        return internalVictor.configClosedloopRamp(secondsFromNeutralToFull);
    }

    public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
        return internalVictor.configPeakOutputForward(percentOut, timeoutMs);
    }

    public ErrorCode configPeakOutputForward(double percentOut) {
        return internalVictor.configPeakOutputForward(percentOut);
    }

    public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
        return internalVictor.configPeakOutputReverse(percentOut, timeoutMs);
    }

    public ErrorCode configPeakOutputReverse(double percentOut) {
        return internalVictor.configPeakOutputReverse(percentOut);
    }

    public ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {
        return internalVictor.configNominalOutputForward(percentOut, timeoutMs);
    }

    public ErrorCode configNominalOutputForward(double percentOut) {
        return internalVictor.configNominalOutputForward(percentOut);
    }

    public ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {
        return internalVictor.configNominalOutputReverse(percentOut, timeoutMs);
    }

    public ErrorCode configNominalOutputReverse(double percentOut) {
        return internalVictor.configNominalOutputReverse(percentOut);
    }

    public ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {
        return internalVictor.configNeutralDeadband(percentDeadband, timeoutMs);
    }

    public ErrorCode configNeutralDeadband(double percentDeadband) {
        return internalVictor.configNeutralDeadband(percentDeadband);
    }

    public ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {
        return internalVictor.configVoltageCompSaturation(voltage, timeoutMs);
    }

    public ErrorCode configVoltageCompSaturation(double voltage) {
        return internalVictor.configVoltageCompSaturation(voltage);
    }

    public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {
        return internalVictor.configVoltageMeasurementFilter(filterWindowSamples, timeoutMs);
    }

    public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples) {
        return internalVictor.configVoltageMeasurementFilter(filterWindowSamples);
    }

    public void enableVoltageCompensation(boolean enable) {
        internalVictor.enableVoltageCompensation(enable);
    }

    public double getBusVoltage() {
        return internalVictor.getBusVoltage();
    }

    public double getMotorOutputPercent() {
        return internalVictor.getMotorOutputPercent();
    }

    public double getMotorOutputVoltage() {
        return internalVictor.getMotorOutputVoltage();
    }

    public double getTemperature() {
        return internalVictor.getTemperature();
    }

    public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
        return internalVictor.configSelectedFeedbackSensor(feedbackDevice, pidIdx, timeoutMs);
    }

    public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice) {
        return internalVictor.configSelectedFeedbackSensor(feedbackDevice);
    }

    public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
        return internalVictor.configSelectedFeedbackSensor(feedbackDevice, pidIdx, timeoutMs);
    }

    public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice) {
        return internalVictor.configSelectedFeedbackSensor(feedbackDevice);
    }

    public ErrorCode configSelectedFeedbackCoefficient(double coefficient, int pidIdx, int timeoutMs) {
        return internalVictor.configSelectedFeedbackCoefficient(coefficient, pidIdx, timeoutMs);
    }

    public ErrorCode configSelectedFeedbackCoefficient(double coefficient) {
        return internalVictor.configSelectedFeedbackCoefficient(coefficient);
    }

    public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal,
            int timeoutMs) {
        return internalVictor.configRemoteFeedbackFilter(deviceID, remoteSensorSource, remoteOrdinal, timeoutMs);
    }

    public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource,
            int remoteOrdinal) {
        return internalVictor.configRemoteFeedbackFilter(deviceID, remoteSensorSource, remoteOrdinal);
    }

    public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {
        return internalVictor.configSensorTerm(sensorTerm, feedbackDevice, timeoutMs);
    }

    public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice) {
        return internalVictor.configSensorTerm(sensorTerm, feedbackDevice);
    }

    public ErrorCode configSensorTerm(SensorTerm sensorTerm, RemoteFeedbackDevice feedbackDevice, int timeoutMs) {
        return internalVictor.configSensorTerm(sensorTerm, feedbackDevice, timeoutMs);
    }

    public ErrorCode configSensorTerm(SensorTerm sensorTerm, RemoteFeedbackDevice feedbackDevice) {
        return internalVictor.configSensorTerm(sensorTerm, feedbackDevice);
    }

    public double getSelectedSensorPosition(int pidIdx) {
        return internalVictor.getSelectedSensorPosition(pidIdx);
    }

    public double getSelectedSensorPosition() {
        return internalVictor.getSelectedSensorPosition();
    }

    public double getSelectedSensorVelocity(int pidIdx) {
        return internalVictor.getSelectedSensorVelocity(pidIdx);
    }

    public double getSelectedSensorVelocity() {
        return internalVictor.getSelectedSensorVelocity();
    }

    public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs) {
        return internalVictor.setSelectedSensorPosition(sensorPos, pidIdx, timeoutMs);
    }

    public ErrorCode setSelectedSensorPosition(int sensorPos) {
        return internalVictor.setSelectedSensorPosition(sensorPos);
    }

    public ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {
        return internalVictor.setControlFramePeriod(frame, periodMs);
    }

    public ErrorCode setControlFramePeriod(int frame, int periodMs) {
        return internalVictor.setControlFramePeriod(frame, periodMs);
    }

    public ErrorCode setStatusFramePeriod(int frameValue, int periodMs, int timeoutMs) {
        return internalVictor.setStatusFramePeriod(frameValue, periodMs, timeoutMs);
    }

    public ErrorCode setStatusFramePeriod(int frameValue, int periodMs) {
        return internalVictor.setStatusFramePeriod(frameValue, periodMs);
    }

    public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {
        return internalVictor.setStatusFramePeriod(frame, periodMs, timeoutMs);
    }

    public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs) {
        return internalVictor.setStatusFramePeriod(frame, periodMs);
    }

    public int getStatusFramePeriod(int frame, int timeoutMs) {
        return internalVictor.getStatusFramePeriod(frame, timeoutMs);
    }

    public int getStatusFramePeriod(int frame) {
        return internalVictor.getStatusFramePeriod(frame);
    }

    public int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {
        return internalVictor.getStatusFramePeriod(frame, timeoutMs);
    }

    public int getStatusFramePeriod(StatusFrame frame) {
        return internalVictor.getStatusFramePeriod(frame);
    }

    public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {
        return internalVictor.getStatusFramePeriod(frame, timeoutMs);
    }

    public int getStatusFramePeriod(StatusFrameEnhanced frame) {
        return internalVictor.getStatusFramePeriod(frame);
    }

    @SuppressWarnings("deprecation")
    public ErrorCode configVelocityMeasurementPeriod(com.ctre.phoenix.motorcontrol.VelocityMeasPeriod period, int timeoutMs) {
        return internalVictor.configVelocityMeasurementPeriod(period, timeoutMs);
    }

    @SuppressWarnings("deprecation")
    public ErrorCode configVelocityMeasurementPeriod(com.ctre.phoenix.motorcontrol.VelocityMeasPeriod period) {
        return internalVictor.configVelocityMeasurementPeriod(period);
    }

    public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
        return internalVictor.configVelocityMeasurementWindow(windowSize, timeoutMs);
    }

    public ErrorCode configVelocityMeasurementWindow(int windowSize) {
        return internalVictor.configVelocityMeasurementWindow(windowSize);
    }

    public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs) {
        return internalVictor.configForwardLimitSwitchSource(type, normalOpenOrClose, deviceID, timeoutMs);
    }

    public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID) {
        return internalVictor.configForwardLimitSwitchSource(type, normalOpenOrClose, deviceID);
    }

    public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs) {
        return internalVictor.configReverseLimitSwitchSource(type, normalOpenOrClose, deviceID, timeoutMs);
    }

    public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID) {
        return internalVictor.configReverseLimitSwitchSource(type, normalOpenOrClose, deviceID);
    }

    public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int timeoutMs) {
        return internalVictor.configForwardLimitSwitchSource(type, normalOpenOrClose, timeoutMs);
    }

    public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose) {
        return internalVictor.configForwardLimitSwitchSource(type, normalOpenOrClose);
    }

    public void overrideLimitSwitchesEnable(boolean enable) {
        internalVictor.overrideLimitSwitchesEnable(enable);
    }

    public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit, int timeoutMs) {
        return internalVictor.configForwardSoftLimitThreshold(forwardSensorLimit, timeoutMs);
    }

    public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit) {
        return internalVictor.configForwardSoftLimitThreshold(forwardSensorLimit);
    }

    public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit, int timeoutMs) {
        return internalVictor.configReverseSoftLimitThreshold(reverseSensorLimit, timeoutMs);
    }

    public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit) {
        return internalVictor.configReverseSoftLimitThreshold(reverseSensorLimit);
    }

    public ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {
        return internalVictor.configForwardSoftLimitEnable(enable, timeoutMs);
    }

    public ErrorCode configForwardSoftLimitEnable(boolean enable) {
        return internalVictor.configForwardSoftLimitEnable(enable);
    }

    public ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {
        return internalVictor.configReverseSoftLimitEnable(enable, timeoutMs);
    }

    public ErrorCode configReverseSoftLimitEnable(boolean enable) {
        return internalVictor.configReverseSoftLimitEnable(enable);
    }

    public void overrideSoftLimitsEnable(boolean enable) {
        internalVictor.overrideSoftLimitsEnable(enable);
    }

    public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
        return internalVictor.config_kP(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_kP(int slotIdx, double value) {
        return internalVictor.config_kP(slotIdx, value);
    }

    public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
        return internalVictor.config_kI(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_kI(int slotIdx, double value) {
        return internalVictor.config_kI(slotIdx, value);
    }

    public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
        return internalVictor.config_kD(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_kD(int slotIdx, double value) {
        return internalVictor.config_kD(slotIdx, value);
    }

    public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
        return internalVictor.config_kF(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_kF(int slotIdx, double value) {
        return internalVictor.config_kF(slotIdx, value);
    }

    public ErrorCode config_IntegralZone(int slotIdx, int izone, int timeoutMs) {
        return internalVictor.config_IntegralZone(slotIdx, izone, timeoutMs);
    }

    public ErrorCode config_IntegralZone(int slotIdx, int izone) {
        return internalVictor.config_IntegralZone(slotIdx, izone);
    }

    public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableClosedLoopError, int timeoutMs) {
        return internalVictor.configAllowableClosedloopError(slotIdx, allowableClosedLoopError, timeoutMs);
    }

    public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableClosedLoopError) {
        return internalVictor.configAllowableClosedloopError(slotIdx, allowableClosedLoopError);
    }

    public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {
        return internalVictor.configMaxIntegralAccumulator(slotIdx, iaccum, timeoutMs);
    }

    public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum) {
        return internalVictor.configMaxIntegralAccumulator(slotIdx, iaccum);
    }

    public ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut, int timeoutMs) {
        return internalVictor.configClosedLoopPeakOutput(slotIdx, percentOut, timeoutMs);
    }

    public ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut) {
        return internalVictor.configClosedLoopPeakOutput(slotIdx, percentOut);
    }

    public ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs, int timeoutMs) {
        return internalVictor.configClosedLoopPeriod(slotIdx, loopTimeMs, timeoutMs);
    }

    public ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs) {
        return internalVictor.configClosedLoopPeriod(slotIdx, loopTimeMs);
    }

    public ErrorCode configAuxPIDPolarity(boolean invert, int timeoutMs) {
        return internalVictor.configAuxPIDPolarity(invert, timeoutMs);
    }

    public ErrorCode configAuxPIDPolarity(boolean invert) {
        return internalVictor.configAuxPIDPolarity(invert);
    }

    public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {
        return internalVictor.setIntegralAccumulator(iaccum, pidIdx, timeoutMs);
    }

    public ErrorCode setIntegralAccumulator(double iaccum) {
        return internalVictor.setIntegralAccumulator(iaccum);
    }

    public double getClosedLoopError(int pidIdx) {
        return internalVictor.getClosedLoopError(pidIdx);
    }

    public double getClosedLoopError() {
        return internalVictor.getClosedLoopError();
    }

    public double getIntegralAccumulator(int pidIdx) {
        return internalVictor.getIntegralAccumulator(pidIdx);
    }

    public double getIntegralAccumulator() {
        return internalVictor.getIntegralAccumulator();
    }

    public double getErrorDerivative(int pidIdx) {
        return internalVictor.getErrorDerivative(pidIdx);
    }

    public double getErrorDerivative() {
        return internalVictor.getErrorDerivative();
    }

    public void selectProfileSlot(int slotIdx, int pidIdx) {
        internalVictor.selectProfileSlot(slotIdx, pidIdx);
    }

    public double getClosedLoopTarget(int pidIdx) {
        return internalVictor.getClosedLoopTarget(pidIdx);
    }

    public double getClosedLoopTarget() {
        return internalVictor.getClosedLoopTarget();
    }

    public double getActiveTrajectoryPosition() {
        return internalVictor.getActiveTrajectoryPosition();
    }

    public double getActiveTrajectoryPosition(int pidIdx) {
        return internalVictor.getActiveTrajectoryPosition(pidIdx);
    }

    public double getActiveTrajectoryVelocity() {
        return internalVictor.getActiveTrajectoryVelocity();
    }

    public double getActiveTrajectoryVelocity(int pidIdx) {
        return internalVictor.getActiveTrajectoryVelocity(pidIdx);
    }

    public double getActiveTrajectoryArbFeedFwd() {
        return internalVictor.getActiveTrajectoryArbFeedFwd();
    }

    public double getActiveTrajectoryArbFeedFwd(int pidIdx) {
        return internalVictor.getActiveTrajectoryArbFeedFwd(pidIdx);
    }

    public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms, int timeoutMs) {
        return internalVictor.configMotionCruiseVelocity(sensorUnitsPer100ms, timeoutMs);
    }

    public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms) {
        return internalVictor.configMotionCruiseVelocity(sensorUnitsPer100ms);
    }

    public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec, int timeoutMs) {
        return internalVictor.configMotionAcceleration(sensorUnitsPer100msPerSec, timeoutMs);
    }

    public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec) {
        return internalVictor.configMotionAcceleration(sensorUnitsPer100msPerSec);
    }

    public ErrorCode configMotionSCurveStrength(int curveStrength, int timeoutMs) {
        return internalVictor.configMotionSCurveStrength(curveStrength, timeoutMs);
    }

    public ErrorCode configMotionSCurveStrength(int curveStrength) {
        return internalVictor.configMotionSCurveStrength(curveStrength);
    }

    public ErrorCode clearMotionProfileTrajectories() {
        return internalVictor.clearMotionProfileTrajectories();
    }

    public int getMotionProfileTopLevelBufferCount() {
        return internalVictor.getMotionProfileTopLevelBufferCount();
    }

    public ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {
        return internalVictor.pushMotionProfileTrajectory(trajPt);
    }

    public ErrorCode startMotionProfile(BufferedTrajectoryPointStream stream, int minBufferedPts,
            ControlMode motionProfControlMode) {
        return internalVictor.startMotionProfile(stream, minBufferedPts, motionProfControlMode);
    }

    public boolean isMotionProfileFinished() {
        return internalVictor.isMotionProfileFinished();
    }

    public boolean isMotionProfileTopLevelBufferFull() {
        return internalVictor.isMotionProfileTopLevelBufferFull();
    }

    public void processMotionProfileBuffer() {
        internalVictor.processMotionProfileBuffer();
    }

    public ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {
        return internalVictor.getMotionProfileStatus(statusToFill);
    }

    public ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {
        return internalVictor.clearMotionProfileHasUnderrun(timeoutMs);
    }

    public ErrorCode clearMotionProfileHasUnderrun() {
        return internalVictor.clearMotionProfileHasUnderrun();
    }

    public ErrorCode changeMotionControlFramePeriod(int periodMs) {
        return internalVictor.changeMotionControlFramePeriod(periodMs);
    }

    public ErrorCode configMotionProfileTrajectoryPeriod(int baseTrajDurationMs, int timeoutMs) {
        return internalVictor.configMotionProfileTrajectoryPeriod(baseTrajDurationMs, timeoutMs);
    }

    public ErrorCode configMotionProfileTrajectoryPeriod(int baseTrajDurationMs) {
        return internalVictor.configMotionProfileTrajectoryPeriod(baseTrajDurationMs);
    }

    public ErrorCode configMotionProfileTrajectoryInterpolationEnable(boolean enable, int timeoutMs) {
        return internalVictor.configMotionProfileTrajectoryInterpolationEnable(enable, timeoutMs);
    }

    public ErrorCode configMotionProfileTrajectoryInterpolationEnable(boolean enable) {
        return internalVictor.configMotionProfileTrajectoryInterpolationEnable(enable);
    }

    public ErrorCode configFeedbackNotContinuous(boolean feedbackNotContinuous, int timeoutMs) {
        return internalVictor.configFeedbackNotContinuous(feedbackNotContinuous, timeoutMs);
    }

    public ErrorCode configRemoteSensorClosedLoopDisableNeutralOnLOS(boolean remoteSensorClosedLoopDisableNeutralOnLOS,
            int timeoutMs) {
        return internalVictor.configRemoteSensorClosedLoopDisableNeutralOnLOS(remoteSensorClosedLoopDisableNeutralOnLOS,
                timeoutMs);
    }

    public ErrorCode configClearPositionOnLimitF(boolean clearPositionOnLimitF, int timeoutMs) {
        return internalVictor.configClearPositionOnLimitF(clearPositionOnLimitF, timeoutMs);
    }

    public ErrorCode configClearPositionOnLimitR(boolean clearPositionOnLimitR, int timeoutMs) {
        return internalVictor.configClearPositionOnLimitR(clearPositionOnLimitR, timeoutMs);
    }

    public ErrorCode configClearPositionOnQuadIdx(boolean clearPositionOnQuadIdx, int timeoutMs) {
        return internalVictor.configClearPositionOnQuadIdx(clearPositionOnQuadIdx, timeoutMs);
    }

    public ErrorCode configLimitSwitchDisableNeutralOnLOS(boolean limitSwitchDisableNeutralOnLOS, int timeoutMs) {
        return internalVictor.configLimitSwitchDisableNeutralOnLOS(limitSwitchDisableNeutralOnLOS, timeoutMs);
    }

    public ErrorCode configSoftLimitDisableNeutralOnLOS(boolean softLimitDisableNeutralOnLOS, int timeoutMs) {
        return internalVictor.configSoftLimitDisableNeutralOnLOS(softLimitDisableNeutralOnLOS, timeoutMs);
    }

    public ErrorCode configPulseWidthPeriod_EdgesPerRot(int pulseWidthPeriod_EdgesPerRot, int timeoutMs) {
        return internalVictor.configPulseWidthPeriod_EdgesPerRot(pulseWidthPeriod_EdgesPerRot, timeoutMs);
    }

    public ErrorCode configPulseWidthPeriod_FilterWindowSz(int pulseWidthPeriod_FilterWindowSz, int timeoutMs) {
        return internalVictor.configPulseWidthPeriod_FilterWindowSz(pulseWidthPeriod_FilterWindowSz, timeoutMs);
    }

    public ErrorCode getLastError() {
        return internalVictor.getLastError();
    }

    public ErrorCode getFaults(Faults toFill) {
        return internalVictor.getFaults(toFill);
    }

    public ErrorCode getStickyFaults(StickyFaults toFill) {
        return internalVictor.getStickyFaults(toFill);
    }

    public ErrorCode clearStickyFaults(int timeoutMs) {
        return internalVictor.clearStickyFaults(timeoutMs);
    }

    public ErrorCode clearStickyFaults() {
        return internalVictor.clearStickyFaults();
    }

    public int getFirmwareVersion() {
        return internalVictor.getFirmwareVersion();
    }

    public boolean hasResetOccurred() {
        return internalVictor.hasResetOccurred();
    }

    public ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {
        return internalVictor.configSetCustomParam(newValue, paramIndex, timeoutMs);
    }

    public ErrorCode configSetCustomParam(int newValue, int paramIndex) {
        return internalVictor.configSetCustomParam(newValue, paramIndex);
    }

    public int configGetCustomParam(int paramIndex, int timeoutMs) {
        return internalVictor.configGetCustomParam(paramIndex, timeoutMs);
    }

    public int configGetCustomParam(int paramIndex) {
        return internalVictor.configGetCustomParam(paramIndex);
    }

    public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
        return internalVictor.configSetParameter(param, value, subValue, ordinal, timeoutMs);
    }

    public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal) {
        return internalVictor.configSetParameter(param, value, subValue, ordinal);
    }

    public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs) {
        return internalVictor.configSetParameter(param, value, subValue, ordinal, timeoutMs);
    }

    public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal) {
        return internalVictor.configSetParameter(param, value, subValue, ordinal);
    }

    public double configGetParameter(ParamEnum param, int ordinal, int timeoutMs) {
        return internalVictor.configGetParameter(param, ordinal, timeoutMs);
    }

    public double configGetParameter(ParamEnum param, int ordinal) {
        return internalVictor.configGetParameter(param, ordinal);
    }

    public double configGetParameter(int param, int ordinal, int timeoutMs) {
        return internalVictor.configGetParameter(param, ordinal, timeoutMs);
    }

    public double configGetParameter(int param, int ordinal) {
        return internalVictor.configGetParameter(param, ordinal);
    }

    public int getBaseID() {
        return internalVictor.getBaseID();
    }

    public ControlMode getControlMode() {
        return internalVictor.getControlMode();
    }

    public void follow(IMotorController masterToFollow, FollowerType followerType) {
        internalVictor.follow(masterToFollow, followerType);
    }

    public void follow(IMotorController masterToFollow) {
        internalVictor.follow(masterToFollow);
    }

    public String toString() {
        return internalVictor.toString();
    }

    public void valueUpdated() {
        internalVictor.valueUpdated();
    }

    @Deprecated
    public ErrorCode configureSlot(SlotConfiguration slot) {
        return internalVictor.configureSlot(slot);
    }

    @Deprecated
    public ErrorCode configureSlot(SlotConfiguration slot, int slotIdx, int timeoutMs) {
        return internalVictor.configureSlot(slot, slotIdx, timeoutMs);
    }

    public void getSlotConfigs(SlotConfiguration slot, int slotIdx, int timeoutMs) {
        internalVictor.getSlotConfigs(slot, slotIdx, timeoutMs);
    }

    public void getSlotConfigs(SlotConfiguration slot) {
        internalVictor.getSlotConfigs(slot);
    }

    @Deprecated
    public ErrorCode configureFilter(FilterConfiguration filter, int ordinal, int timeoutMs,
            boolean enableOptimizations) {
        return internalVictor.configureFilter(filter, ordinal, timeoutMs, enableOptimizations);
    }

    @Deprecated
    public ErrorCode configureFilter(FilterConfiguration filter, int ordinal, int timeoutMs) {
        return internalVictor.configureFilter(filter, ordinal, timeoutMs);
    }

    @Deprecated
    public ErrorCode configureFilter(FilterConfiguration filter) {
        return internalVictor.configureFilter(filter);
    }

    public void getFilterConfigs(FilterConfiguration filter, int ordinal, int timeoutMs) {
        internalVictor.getFilterConfigs(filter, ordinal, timeoutMs);
    }


    public void getFilterConfigs(FilterConfiguration filter) {
        internalVictor.getFilterConfigs(filter);
    }
    
    ///
    // The Victor can't do anything below this line.
    ///

    @Override
    public double getOutputCurrent() {
        return 0;
    }

    @Override
    public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
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
    public int getPulseWidthRiseToFallUs() {
        return 0;
    }

    @Override
    public ErrorCode configSupplyCurrentLimit(SupplyCurrentLimitConfiguration currLimitCfg, int timeoutMs) {
        return null;
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
    public double getActiveTrajectoryHeading() {
        return 0;
    }

    //CHECKSTYLE:ON
}