package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.ParamEnum;
import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlFrame;
import com.ctre.phoenix.motorcontrol.ControlMode;
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

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.properties.XPropertyManager;

public class SpeedControllerAppearingAsTalonWPIAdapter extends XCANTalon {

	SpeedController internalSpeedController;
	int deviceId;
	
	@Inject
	public SpeedControllerAppearingAsTalonWPIAdapter(int deviceId, XPropertyManager propMan) {
		super(deviceId, propMan);
		// TODO Auto-generated constructor stub
		
		internalSpeedController = new Victor(deviceId);
		this.deviceId = deviceId;
	}

	@Override
	public void set(ControlMode Mode, double demand) {
		internalSpeedController.set(demand);		
	}

	@Override
	public void set(ControlMode Mode, double demand0, double demand1) {
		this.simpleSet(demand0);
		
	}

	@Override
	public void neutralOutput() {
		// TODO Auto-generated method stub
		this.simpleSet(0);
	}

	@Override
	public void setNeutralMode(NeutralMode neutralMode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSensorPhase(boolean PhaseSensor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInverted(boolean invert) {
		// TODO Auto-generated method stub
		internalSpeedController.setInverted(invert);
	}

	@Override
	public boolean getInverted() {
		// TODO Auto-generated method stub
		return internalSpeedController.getInverted();
	}

	@Override
	public ErrorCode configOpenloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configClosedloopRamp(double secondsFromNeutralToFull, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configNominalOutputForward(double percentOut, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configNominalOutputReverse(double percentOut, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configNeutralDeadband(double percentDeadband, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configVoltageCompSaturation(double voltage, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configVoltageMeasurementFilter(int filterWindowSamples, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enableVoltageCompensation(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getBusVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMotorOutputPercent() {
		// TODO Auto-generated method stub
		return internalSpeedController.get();
	}

	@Override
	public double getMotorOutputVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getOutputCurrent() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getTemperature() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorCode configSelectedFeedbackSensor(RemoteFeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configRemoteFeedbackFilter(int deviceID, RemoteSensorSource remoteSensorSource, int remoteOrdinal,
			int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configSensorTerm(SensorTerm sensorTerm, FeedbackDevice feedbackDevice, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configSelectedFeedbackSensor(FeedbackDevice feedbackDevice, int pidIdx, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSelectedSensorPosition(int pidIdx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getSelectedSensorVelocity(int pidIdx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorCode setSelectedSensorPosition(int sensorPos, int pidIdx, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode setControlFramePeriod(ControlFrame frame, int periodMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode setStatusFramePeriod(StatusFrame frame, int periodMs, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode setStatusFramePeriod(StatusFrameEnhanced frame, int periodMs, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStatusFramePeriod(StatusFrame frame, int timeoutMs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getStatusFramePeriod(StatusFrameEnhanced frame, int timeoutMs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorCode configVelocityMeasurementPeriod(VelocityMeasPeriod period, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configVelocityMeasurementWindow(int windowSize, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configForwardLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
			int deviceID, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configReverseLimitSwitchSource(RemoteLimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
			int deviceID, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void overrideLimitSwitchesEnable(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ErrorCode configForwardLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
			int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configReverseLimitSwitchSource(LimitSwitchSource type, LimitSwitchNormal normalOpenOrClose,
			int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configForwardSoftLimitThreshold(int forwardSensorLimit, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configReverseSoftLimitThreshold(int reverseSensorLimit, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configForwardSoftLimitEnable(boolean enable, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configReverseSoftLimitEnable(boolean enable, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void overrideSoftLimitsEnable(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ErrorCode configPeakCurrentLimit(int amps, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configPeakCurrentDuration(int milliseconds, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configContinuousCurrentLimit(int amps, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void enableCurrentLimit(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode config_IntegralZone(int slotIdx, int izone, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configAllowableClosedloopError(int slotIdx, int allowableCloseLoopError, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configMaxIntegralAccumulator(int slotIdx, double iaccum, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode setIntegralAccumulator(double iaccum, int pidIdx, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getClosedLoopError(int pidIdx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getIntegralAccumulator(int pidIdx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getErrorDerivative(int pidIdx) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void selectProfileSlot(int slotIdx, int pidIdx) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getActiveTrajectoryPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getActiveTrajectoryVelocity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getActiveTrajectoryHeading() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorCode configMotionCruiseVelocity(int sensorUnitsPer100ms, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configMotionAcceleration(int sensorUnitsPer100msPerSec, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode clearMotionProfileTrajectories() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMotionProfileTopLevelBufferCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorCode pushMotionProfileTrajectory(TrajectoryPoint trajPt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isMotionProfileTopLevelBufferFull() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void processMotionProfileBuffer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ErrorCode getMotionProfileStatus(MotionProfileStatus statusToFill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode clearMotionProfileHasUnderrun(int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode changeMotionControlFramePeriod(int periodMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode getLastError() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode getFaults(Faults toFill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode getStickyFaults(StickyFaults toFill) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode clearStickyFaults(int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getFirmwareVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasResetOccurred() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ErrorCode configSetCustomParam(int newValue, int paramIndex, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int configGetCustomParam(int paramIndex, int timoutMs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ErrorCode configSetParameter(ParamEnum param, double value, int subValue, int ordinal, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ErrorCode configSetParameter(int param, double value, int subValue, int ordinal, int timeoutMs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double configGetParameter(ParamEnum paramEnum, int ordinal, int timeoutMs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double configGetParameter(int paramEnum, int ordinal, int timeoutMs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getBaseID() {
		return deviceId;
	}

	@Override
	public int getDeviceID() {
		return deviceId;
	}

	@Override
	public void follow(IMotorController masterToFollow) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void valueUpdated() {
		// TODO Auto-generated method stub
		
	}

}
