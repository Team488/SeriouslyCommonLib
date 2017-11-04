package xbot.common.controls.actuators.wpi_adapters;

import com.ctre.CANTalon;
import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.FeedbackDeviceStatus;
import com.ctre.CANTalon.StatusFrameRate;
import com.ctre.CANTalon.TalonControlMode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.actuators.XCANTalon;
import xbot.common.properties.XPropertyManager;

public class CANTalonWPIAdapter extends XCANTalon {

    private CANTalon internalTalon;

    @Inject
    public CANTalonWPIAdapter(@Assisted("deviceId") int deviceId, XPropertyManager propMan) {
        super(deviceId, propMan);
        internalTalon = new CANTalon(deviceId);
        
        LiveWindow.addActuator("CANTalon", deviceId, this.getLiveWindowSendable());
    }

    @Override
    public boolean isEnabled() {
        return internalTalon.isEnabled();
    }

    @Override
    public void enable() {
        internalTalon.enable();
    }

    @Override
    public void disable() {
        internalTalon.disable();
    }

    @Override
    public void setProfile(int profile) {
        internalTalon.setProfile(profile);
    }

    @Override
    public TalonControlMode getControlMode() {
        return internalTalon.getControlMode();
    }

    @Override
    public void setControlMode(TalonControlMode controlMode) {
        internalTalon.changeControlMode(controlMode);
    }

    @Override
    public boolean getBrakeEnableDuringNeutral() {
        return internalTalon.getBrakeEnableDuringNeutral();
    }

    @Override
    public void setBrakeEnableDuringNeutral(boolean brake) {
        internalTalon.enableBrakeMode(brake);

    }

    @Override
    public void setStatusFrameRateMs(StatusFrameRate stateFrame, int periodMs) {
        internalTalon.setStatusFrameRateMs(stateFrame, periodMs);
    }

    @Override
    public void reset() {
        internalTalon.reset();
    }

    @Override
    public int getDeviceID() {
        return internalTalon.getDeviceID();
    }

    @Override
    public double getOutputCurrent() {
        return internalTalon.getOutputCurrent();
    }

    @Override
    public double getOutputVoltage() {
        return internalTalon.getOutputVoltage();
    }

    @Override
    public double getTemperature() {
        return internalTalon.getTemperature();
    }

    @Override
    public double getBusVoltage() {
        return internalTalon.getBusVoltage();
    }

    @Override
    public long getFirmwareVersion() {
        return internalTalon.GetFirmwareVersion();
    }

    @Override
    public void clearStickyFaults() {
        internalTalon.clearStickyFaults();
    }

    @Override
    public int getFaultForwardLim() {
        return internalTalon.getFaultForLim();
    }

    @Override
    public int getFaultForwardSoftLim() {
        return internalTalon.getFaultForSoftLim();
    }

    @Override
    public int getFaultHardwareFailure() {
        return internalTalon.getFaultHardwareFailure();
    }

    @Override
    public int getFaultOverTemp() {
        return internalTalon.getFaultOverTemp();
    }

    @Override
    public int getFaultReverseLim() {
        return internalTalon.getFaultRevLim();
    }

    @Override
    public int getFaultReverseSoftLim() {
        return internalTalon.getFaultRevSoftLim();
    }

    @Override
    public int getFaultUnderVoltage() {
        return internalTalon.getFaultUnderVoltage();
    }

    @Override
    public int getStickyFaultForwardLim() {
        return internalTalon.getStickyFaultForLim();
    }

    @Override
    public int getStickyFaultForwardSoftLim() {
        return internalTalon.getStickyFaultForSoftLim();
    }

    @Override
    public int getStickyFaultOverTemp() {
        return internalTalon.getStickyFaultOverTemp();
    }

    @Override
    public int getStickyFaultReverseLim() {
        return internalTalon.getStickyFaultRevLim();
    }

    @Override
    public int getStickyFaultReverseSoftLim() {
        return internalTalon.getStickyFaultRevSoftLim();
    }

    @Override
    public int getStickyFaultUnderVoltage() {
        return internalTalon.getStickyFaultUnderVoltage();
    }

    @Override
    public double getP() {
        return internalTalon.getP();
    }

    @Override
    public double getI() {
        return internalTalon.getI();
    }

    @Override
    public double getD() {
        return internalTalon.getD();
    }

    @Override
    public double getF() {
        return internalTalon.getF();
    }

    @Override
    public void setP(double p) {
        internalTalon.setP(p);
    }

    @Override
    public void setI(double i) {
        internalTalon.setI(i);
    }

    @Override
    public void setD(double d) {
        internalTalon.setD(d);
    }

    @Override
    public void setF(double f) {
        internalTalon.setF(f);
    }

    @Override
    public void setPID(double p, double i, double d) {
        internalTalon.setPID(p, i, d);
    }

    @Override
    public void clearIAccum() {
        internalTalon.clearIAccum();
    }

    @Override
    public int getClosedLoopError() {
        return internalTalon.getClosedLoopError();
    }

    @Override
    public void setAllowableClosedLoopError(int allowableError) {
        internalTalon.setAllowableClosedLoopErr(allowableError);
    }

    @Override
    public double getIZone() {
        return internalTalon.getIZone();
    }

    @Override
    public void setIZone(int iZone) {
        internalTalon.setIZone(iZone);
    }

    @Override
    public long getIAccum() {
        return internalTalon.GetIaccum();
    }

    @Override
    public void setClosedLoopRampRate(double rampRate) {
        internalTalon.setCloseLoopRampRate(rampRate);
    }

    @Override
    public FeedbackDeviceStatus isSensorPresent(FeedbackDevice feedbackDevice) {
        return internalTalon.isSensorPresent(feedbackDevice);
    }

    @Override
    public void setFeedbackDevice(FeedbackDevice device) {
        internalTalon.setFeedbackDevice(device);
    }

    @Override
    public void configEncoderCodesPerRev(int codesPerRev) {
        internalTalon.configEncoderCodesPerRev(codesPerRev);
    }

    @Override
    public void configPotentiometerTurns(int turns) {
        internalTalon.configPotentiometerTurns(turns);
    }

    @Override
    public double getPosition() {
        return internalTalon.getPosition();
    }

    @Override
    public void setPosition(double pos) {
        internalTalon.setPosition(pos);
    }

    @Override
    public double getSpeed() {
        return internalTalon.getSpeed();
    }

    @Override
    public int getAnalogPosition() {
        return internalTalon.getAnalogInPosition();
    }

    @Override
    public void setAnalogPosition(int newPosition) {
        internalTalon.setAnalogPosition(newPosition);
    }

    @Override
    public int getAnalogPositionRaw() {
        return internalTalon.getAnalogInRaw();
    }

    @Override
    public int getAnalogSpeed() {
        return internalTalon.getAnalogInVelocity();
    }

    @Override
    public int getEncoderPosition() {
        return internalTalon.getEncPosition();
    }

    @Override
    public void setEncoderPosition(int newPosition) {
        internalTalon.setEncPosition(newPosition);
    }

    @Override
    public int getEncoderSpeed() {
        return internalTalon.getEncVelocity();
    }

    @Override
    public void reverseSensor(boolean flip) {
        internalTalon.reverseSensor(flip);
    }

    @Override
    public void enableZeroSensorPositionOnIndex(boolean enable, boolean risingEdge) {
        internalTalon.enableZeroSensorPositionOnIndex(enable, risingEdge);
    }

    @Override
    public int getNumberOfQuadIndexRises() {
        return internalTalon.getNumberOfQuadIdxRises();
    }

    @Override
    public boolean getInverted() {
        return internalTalon.getInverted();
    }

    @Override
    public void setInverted(boolean isInverted) {
        internalTalon.setInverted(isInverted);
    }

    @Override
    public void setVoltageCompensationRampRate(double rampRate) {
        internalTalon.setVoltageCompensationRampRate(rampRate);
    }

    @Override
    public void configNominalOutputVoltage(double forwardVoltage, double reverseVoltage) {
        internalTalon.configNominalOutputVoltage(forwardVoltage, reverseVoltage);

    }

    @Override
    public void configPeakOutputVoltage(double forwardVoltage, double reverseVoltage) {
        internalTalon.configPeakOutputVoltage(forwardVoltage, reverseVoltage);

    }

    @Override
    public int getForwardSoftLimit() {
        return internalTalon.getForwardSoftLimit();
    }

    @Override
    public int getReverseSoftLimit() {
        return internalTalon.getReverseSoftLimit();
    }

    @Override
    public void setForwardSoftLimit(double forwardLimit) {
        internalTalon.setForwardSoftLimit(forwardLimit);
    }

    @Override
    public void setReverseSoftLimit(double reverseLimit) {
        internalTalon.setReverseSoftLimit(reverseLimit);
    }

    @Override
    public boolean isForwardSoftLimitEnabled() {
        return internalTalon.isForwardSoftLimitEnabled();
    }

    @Override
    public boolean isReverseSoftLimitEnabled() {
        return internalTalon.isReverseSoftLimitEnabled();
    }

    @Override
    public void enableForwardSoftLimit(boolean enable) {
        internalTalon.enableForwardSoftLimit(enable);
    }

    @Override
    public void enableReverseSoftLimit(boolean enable) {
        internalTalon.enableReverseSoftLimit(enable);
    }

    @Override
    public void enableLimitSwitches(boolean forwardEnabled, boolean reverseEnabled) {
        internalTalon.enableLimitSwitch(forwardEnabled, reverseEnabled);
    }

    @Override
    public boolean isForwardLimitSwitchClosed() {
        return internalTalon.isFwdLimitSwitchClosed();
    }

    @Override
    public boolean isReverseLimitSwitchClosed() {
        return internalTalon.isRevLimitSwitchClosed();
    }

    @Override
    public void configForwardLimitSwitchNormallyOpen(boolean normallyOpen) {
        internalTalon.ConfigFwdLimitSwitchNormallyOpen(normallyOpen);
    }

    @Override
    public void configReverseLimitSwitchNormallyOpen(boolean normallyOpen) {
        internalTalon.ConfigRevLimitSwitchNormallyOpen(normallyOpen);
    }

    @Override
    public double get() {
        return internalTalon.get();
    }

    @Override
    public void set(double outputValue) {
        internalTalon.set(outputValue);
    }

    @Override
    public LiveWindowSendable getLiveWindowSendable() {
        return internalTalon;
    }
}
