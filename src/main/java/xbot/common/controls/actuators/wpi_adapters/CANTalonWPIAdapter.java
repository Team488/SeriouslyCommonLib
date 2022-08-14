package xbot.common.controls.actuators.wpi_adapters;

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
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.ctre.phoenix.motorcontrol.SensorTerm;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.BaseTalon;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XCANTalon;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANTalonInfo;
import xbot.common.properties.PropertyFactory;

public class CANTalonWPIAdapter extends XCANTalon {

    private TalonSRX internalTalon;
    SensorCollection sensorCollection;

    @AssistedFactory
    public abstract static class CANTalonWPIAdapterFactory implements XCANTalonFactory {
        public abstract CANTalonWPIAdapter create(@Assisted("deviceInfo") CANTalonInfo deviceInfo);
    }

    @AssistedInject
    public CANTalonWPIAdapter(@Assisted("deviceInfo") CANTalonInfo deviceInfo, PropertyFactory propMan, DevicePolice police) {
        super(deviceInfo.channel, propMan, police);
        internalTalon = new TalonSRX(deviceInfo.channel);
        setInverted(deviceInfo.inverted);
        if (deviceInfo.feedbackDevice != null) {
            configSelectedFeedbackSensor(deviceInfo.feedbackDevice, 0, 0);
            setSensorPhase(deviceInfo.feedbackDeviceInverted);
        }
    }

    public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
        return internalTalon.setStatusFramePeriod(frame, periodMs, timeoutMs);
    }

    public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {
        return internalTalon.getStatusFramePeriod(frame, timeoutMs);
    }

    @SuppressWarnings("deprecation")
    public ErrorCode configVelocityMeasurementPeriod(com.ctre.phoenix.motorcontrol.VelocityMeasPeriod period, int timeoutMs) {
        return internalTalon.configVelocityMeasurementPeriod(period, timeoutMs);
    }

    public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
        return internalTalon.configVelocityMeasurementWindow(windowSize, timeoutMs);
    }

    public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int timeoutMs) {
        return internalTalon.configForwardLimitSwitchSource(type, normalOpenOrClose, timeoutMs);
    }

    public ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int timeoutMs) {
        return internalTalon.configReverseLimitSwitchSource(type, normalOpenOrClose, timeoutMs);
    }

    public ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {
        return internalTalon.configPeakCurrentLimit(amps, timeoutMs);
    }

    public long getHandle() {
        return internalTalon.getHandle();
    }

    public int getDeviceID() {
        return internalTalon.getDeviceID();
    }

    public void set(ControlMode mode, double outputValue) {
        internalTalon.set(mode, outputValue);
    }

    public ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {
        return internalTalon.configPeakCurrentDuration(milliseconds, timeoutMs);
    }

    public ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
        return internalTalon.configContinuousCurrentLimit(amps, timeoutMs);
    }
/*
    public boolean equals(Object obj) {
        return internalTalon.equals(obj);
    }
*/
    public void enableCurrentLimit(boolean enable) {
        internalTalon.enableCurrentLimit(enable);
    }

    public void neutralOutput() {
        internalTalon.neutralOutput();
    }

    public void setNeutralMode(NeutralMode neutralMode) {
        internalTalon.setNeutralMode(neutralMode);
    }

    public void setSensorPhase(boolean PhaseSensor) {
        internalTalon.setSensorPhase(PhaseSensor);
    }

    public void setInverted(boolean invert) {
        internalTalon.setInverted(invert);
    }

    public boolean getInverted() {
        return internalTalon.getInverted();
    }

    public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
        return internalTalon.configOpenloopRamp(secondsFromNeutralToFull, timeoutMs);
    }

    public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
        return internalTalon.configClosedloopRamp(secondsFromNeutralToFull, timeoutMs);
    }

    public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
        return internalTalon.configPeakOutputForward(percentOut, timeoutMs);
    }

    public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
        return internalTalon.configPeakOutputReverse(percentOut, timeoutMs);
    }

    public ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {
        return internalTalon.configNominalOutputForward(percentOut, timeoutMs);
    }

    public ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {
        return internalTalon.configNominalOutputReverse(percentOut, timeoutMs);
    }

    public String toString() {
        return internalTalon.toString();
    }

    public ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {
        return internalTalon.configNeutralDeadband(percentDeadband, timeoutMs);
    }

    public ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {
        return internalTalon.configVoltageCompSaturation(voltage, timeoutMs);
    }

    public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {
        return internalTalon.configVoltageMeasurementFilter(filterWindowSamples, timeoutMs);
    }

    public void enableVoltageCompensation(boolean enable) {
        internalTalon.enableVoltageCompensation(enable);
    }

    public double getBusVoltage() {
        return internalTalon.getBusVoltage();
    }

    public double getMotorOutputPercent() {
        return internalTalon.getMotorOutputPercent();
    }

    public double getMotorOutputVoltage() {
        return internalTalon.getMotorOutputVoltage();
    }

    public double getOutputCurrent() {
        return internalTalon.getStatorCurrent();
    }

    public double getTemperature() {
        return internalTalon.getTemperature();
    }

    public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
        return internalTalon.configSelectedFeedbackSensor(feedbackDevice, pidIdx, timeoutMs);
    }

    public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
        return internalTalon.configSelectedFeedbackSensor(feedbackDevice, pidIdx, timeoutMs);
    }

    public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal,
            int timeoutMs) {
        return internalTalon.configRemoteFeedbackFilter(deviceID, remoteSensorSource, remoteOrdinal, timeoutMs);
    }

    public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {
        return internalTalon.configSensorTerm(sensorTerm, feedbackDevice, timeoutMs);
    }

    public double getSelectedSensorPosition(int pidIdx) {
        return internalTalon.getSelectedSensorPosition(pidIdx);
    }

    public double getSelectedSensorVelocity(int pidIdx) {
        return internalTalon.getSelectedSensorVelocity(pidIdx);
    }

    public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs) {
        return internalTalon.setSelectedSensorPosition(sensorPos, pidIdx, timeoutMs);
    }

    public ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {
        return internalTalon.setControlFramePeriod(frame, periodMs);
    }

    public ErrorCode setControlFramePeriod(int frame, int periodMs) {
        return internalTalon.setControlFramePeriod(frame, periodMs);
    }

    public ErrorCode setStatusFramePeriod(int frameValue, int periodMs, int timeoutMs) {
        return internalTalon.setStatusFramePeriod(frameValue, periodMs, timeoutMs);
    }

    public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {
        return internalTalon.setStatusFramePeriod(frame, periodMs, timeoutMs);
    }

    public int getStatusFramePeriod(int frame, int timeoutMs) {
        return internalTalon.getStatusFramePeriod(frame, timeoutMs);
    }

    public int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {
        return internalTalon.getStatusFramePeriod(frame, timeoutMs);
    }

    public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs) {
        return internalTalon.configForwardLimitSwitchSource(type, normalOpenOrClose, deviceID, timeoutMs);
    }

    public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
            int deviceID, int timeoutMs) {
        return internalTalon.configReverseLimitSwitchSource(type, normalOpenOrClose, deviceID, timeoutMs);
    }

    public void overrideLimitSwitchesEnable(boolean enable) {
        internalTalon.overrideLimitSwitchesEnable(enable);
    }

    public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit, int timeoutMs) {
        return internalTalon.configForwardSoftLimitThreshold(forwardSensorLimit, timeoutMs);
    }

    public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit, int timeoutMs) {
        return internalTalon.configReverseSoftLimitThreshold(reverseSensorLimit, timeoutMs);
    }

    public ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {
        return internalTalon.configForwardSoftLimitEnable(enable, timeoutMs);
    }

    public ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {
        return internalTalon.configReverseSoftLimitEnable(enable, timeoutMs);
    }

    public void overrideSoftLimitsEnable(boolean enable) {
        internalTalon.overrideSoftLimitsEnable(enable);
    }
    
    public boolean isFwdLimitSwitchClosed() {
        return internalTalon.getSensorCollection().isFwdLimitSwitchClosed();
    }
    
    public boolean isRevLimitSwitchClosed() {
        return internalTalon.getSensorCollection().isRevLimitSwitchClosed();
    }

    public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
        return internalTalon.config_kP(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
        return internalTalon.config_kI(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
        return internalTalon.config_kD(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
        return internalTalon.config_kF(slotIdx, value, timeoutMs);
    }

    public ErrorCode config_IntegralZone(int slotIdx, int izone, int timeoutMs) {
        return internalTalon.config_IntegralZone(slotIdx, izone, timeoutMs);
    }

    public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableClosedLoopError, int timeoutMs) {
        return internalTalon.configAllowableClosedloopError(slotIdx, allowableClosedLoopError, timeoutMs);
    }

    public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {
        return internalTalon.configMaxIntegralAccumulator(slotIdx, iaccum, timeoutMs);
    }

    public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {
        return internalTalon.setIntegralAccumulator(iaccum, pidIdx, timeoutMs);
    }

    public double getClosedLoopError(int pidIdx) {
        return internalTalon.getClosedLoopError(pidIdx);
    }

    public double getIntegralAccumulator(int pidIdx) {
        return internalTalon.getIntegralAccumulator(pidIdx);
    }

    public double getErrorDerivative(int pidIdx) {
        return internalTalon.getErrorDerivative(pidIdx);
    }

    public void selectProfileSlot(int slotIdx, int pidIdx) {
        internalTalon.selectProfileSlot(slotIdx, pidIdx);
    }

    public double getActiveTrajectoryPosition() {
        return internalTalon.getActiveTrajectoryPosition();
    }

    public double getActiveTrajectoryVelocity() {
        return internalTalon.getActiveTrajectoryVelocity();
    }

    public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms, int timeoutMs) {
        return internalTalon.configMotionCruiseVelocity(sensorUnitsPer100ms, timeoutMs);
    }

    public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec, int timeoutMs) {
        return internalTalon.configMotionAcceleration(sensorUnitsPer100msPerSec, timeoutMs);
    }

    public ErrorCode clearMotionProfileTrajectories() {
        return internalTalon.clearMotionProfileTrajectories();
    }

    public int getMotionProfileTopLevelBufferCount() {
        return internalTalon.getMotionProfileTopLevelBufferCount();
    }

    public ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {
        return internalTalon.pushMotionProfileTrajectory(trajPt);
    }

    public boolean isMotionProfileTopLevelBufferFull() {
        return internalTalon.isMotionProfileTopLevelBufferFull();
    }

    public void processMotionProfileBuffer() {
        internalTalon.processMotionProfileBuffer();
    }

    public ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {
        return internalTalon.getMotionProfileStatus(statusToFill);
    }

    public ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {
        return internalTalon.clearMotionProfileHasUnderrun(timeoutMs);
    }

    public ErrorCode changeMotionControlFramePeriod(int periodMs) {
        return internalTalon.changeMotionControlFramePeriod(periodMs);
    }

    public ErrorCode getLastError() {
        return internalTalon.getLastError();
    }

    public ErrorCode getFaults(Faults toFill) {
        return internalTalon.getFaults(toFill);
    }

    public ErrorCode getStickyFaults(StickyFaults toFill) {
        return internalTalon.getStickyFaults(toFill);
    }

    public ErrorCode clearStickyFaults(int timeoutMs) {
        return internalTalon.clearStickyFaults(timeoutMs);
    }

    public int getFirmwareVersion() {
        return internalTalon.getFirmwareVersion();
    }

    public boolean hasResetOccurred() {
        return internalTalon.hasResetOccurred();
    }

    public ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {
        return internalTalon.configSetCustomParam(newValue, paramIndex, timeoutMs);
    }

    public int configGetCustomParam(int paramIndex, int timoutMs) {
        return internalTalon.configGetCustomParam(paramIndex, timoutMs);
    }

    public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
        return internalTalon.configSetParameter(param, value, subValue, ordinal, timeoutMs);
    }

    public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs) {
        return internalTalon.configSetParameter(param, value, subValue, ordinal, timeoutMs);
    }

    public double configGetParameter(ParamEnum param, int ordinal, int timeoutMs) {
        return internalTalon.configGetParameter(param, ordinal, timeoutMs);
    }

    public double configGetParameter(int param, int ordinal, int timeoutMs) {
        return internalTalon.configGetParameter(param, ordinal, timeoutMs);
    }

    public int getBaseID() {
        return internalTalon.getBaseID();
    }

    public void follow(IMotorController masterToFollow) {
        internalTalon.follow(masterToFollow);
    }

    public void valueUpdated() {
        internalTalon.valueUpdated();
    }

    public SensorCollection getSensorCollection() {
        return internalTalon.getSensorCollection();
    }

    public ControlMode getControlMode() {
        return internalTalon.getControlMode();
    }

    @Override
    public void set(ControlMode Mode, double demand0, DemandType demand1Type, double demand1) {
        internalTalon.set(Mode, demand0, demand1Type, demand1);
    }

    @Override
    public ErrorCode configSelectedFeedbackCoefficient(double coefficient, int pidIdx, int timeoutMs) {
        return internalTalon.configSelectedFeedbackCoefficient(coefficient, pidIdx, timeoutMs);
    }

    @Override
    public ErrorCode configClosedLoopPeakOutput(int slotIdx, double percentOut, int timeoutMs) {
        return internalTalon.configClosedLoopPeakOutput(slotIdx, percentOut, timeoutMs);
    }

    @Override
    public ErrorCode configClosedLoopPeriod(int slotIdx, int loopTimeMs, int timeoutMs) {
        return internalTalon.configClosedLoopPeriod(slotIdx, loopTimeMs, timeoutMs);
    }

    @Override
    public ErrorCode configAuxPIDPolarity(boolean invert, int timeoutMs) {
        return internalTalon.configAuxPIDPolarity(invert, timeoutMs);
    }

    @Override
    public double getClosedLoopTarget(int pidIdx) {
        return internalTalon.getClosedLoopTarget(pidIdx);
    }

    @Override
    public ErrorCode configMotionProfileTrajectoryPeriod(int baseTrajDurationMs, int timeoutMs) {
        return internalTalon.configMotionProfileTrajectoryPeriod(baseTrajDurationMs, timeoutMs);
	}

    @Override
    public ErrorCode configMotionSCurveStrength(int curveStrength, int timeoutMs) {
        return internalTalon.configMotionSCurveStrength(curveStrength, timeoutMs);
    }

    private SensorCollection getSensorCollectionInstance() {
        if (sensorCollection == null) {
            sensorCollection = internalTalon.getSensorCollection();
        }
        return sensorCollection;
    }

    @Override
    public int getPulseWidthRiseToFallUs() {
        return getSensorCollectionInstance().getPulseWidthRiseToRiseUs();
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
    public double getActiveTrajectoryHeading() {
        return 0;
    }
   
}
