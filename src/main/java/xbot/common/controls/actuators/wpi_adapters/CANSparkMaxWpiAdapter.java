package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANAnalog;
import com.revrobotics.CANAnalog.AnalogMode;
import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANDigitalInput.LimitSwitchPolarity;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANError;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.EncoderType;

import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class CANSparkMaxWpiAdapter extends XCANSparkMax {

    private CANSparkMax internalSpark;

    @Inject
    public CANSparkMaxWpiAdapter(@Assisted("deviceId") int deviceId, PropertyFactory propMan, DevicePolice police) {
        super(deviceId, propMan, police);
        internalSpark = new CANSparkMax(deviceId, MotorType.kBrushless);
    }

    @Override
    public void set(double speed) {
        internalSpark.set(speed);
    }

    @Override
    public void setVoltage(double outputVolts) {
        internalSpark.setVoltage(outputVolts);
    }

    @Override
    public double get() {
        return internalSpark.get();
    }

    @Override
    public void setInverted(boolean isInverted) {
        internalSpark.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return internalSpark.getInverted();
    }

    @Override
    public void disable() {
        internalSpark.disable();
    }

    @Override
    public void stopMotor() {
        internalSpark.stopMotor();
    }

    @Override
    public void pidWrite(double output) {
        internalSpark.pidWrite(output);
    }

    @Override
    public CANEncoder getEncoder() {
        return internalSpark.getEncoder();
    }

    @Override
    public CANEncoder getEncoder(EncoderType sensorType, int counts_per_rev) {
        return internalSpark.getEncoder();
    }

    @Override
    public CANEncoder getAlternateEncoder() {
        return internalSpark.getAlternateEncoder();
    }

    @Override
    public CANEncoder getAlternateEncoder(AlternateEncoderType sensorType, int counts_per_rev) {
        return internalSpark.getAlternateEncoder(sensorType, counts_per_rev);
    }

    @Override
    public CANAnalog getAnalog(AnalogMode mode) {
        return internalSpark.getAnalog(mode);
    }

    @Override
    public CANPIDController getPIDController() {
        return internalSpark.getPIDController();
    }

    @Override
    public CANDigitalInput getForwardLimitSwitch(LimitSwitchPolarity polarity) {
        return internalSpark.getForwardLimitSwitch(polarity);
    }

    @Override
    public CANDigitalInput getReverseLimitSwitch(LimitSwitchPolarity polarity) {
        return internalSpark.getReverseLimitSwitch(polarity);
    }

    @Override
    public CANError setSmartCurrentLimit(int limit) {
        return internalSpark.setSmartCurrentLimit(limit);
    }

    @Override
    public CANError setSmartCurrentLimit(int stallLimit, int freeLimit) {
        return internalSpark.setSmartCurrentLimit(stallLimit, freeLimit);
    }

    @Override
    public CANError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM) {
        return internalSpark.setSmartCurrentLimit(stallLimit, freeLimit, limitRPM);
    }

    @Override
    public CANError setSecondaryCurrentLimit(double limit) {
        return internalSpark.setSecondaryCurrentLimit(limit);
    }

    @Override
    public CANError setSecondaryCurrentLimit(double limit, int chopCycles) {
        return internalSpark.setSecondaryCurrentLimit(limit, chopCycles);
    }

    @Override
    public CANError setIdleMode(IdleMode mode) {
        return internalSpark.setIdleMode(mode);
    }

    @Override
    public IdleMode getIdleMode() {
        return internalSpark.getIdleMode();
    }

    @Override
    public CANError enableVoltageCompensation(double nominalVoltage) {
        return internalSpark.enableVoltageCompensation(nominalVoltage);
    }

    @Override
    public CANError disableVoltageCompensation() {
        return internalSpark.disableVoltageCompensation();
    }

    @Override
    public double getVoltageCompensationNominalVoltage() {
        return internalSpark.getVoltageCompensationNominalVoltage();
    }

    @Override
    public CANError setOpenLoopRampRate(double rate) {
        return internalSpark.setOpenLoopRampRate(rate);
    }

    @Override
    public CANError setClosedLoopRampRate(double rate) {
        return internalSpark.setClosedLoopRampRate(rate);
    }

    @Override
    public double getOpenLoopRampRate() {
        return internalSpark.getOpenLoopRampRate();
    }

    @Override
    public double getClosedLoopRampRate() {
        return internalSpark.getClosedLoopRampRate();
    }

    @Override
    public CANError follow(CANSparkMax leader) {
        return internalSpark.follow(leader);
    }

    @Override
    public CANError follow(CANSparkMax leader, boolean invert) {
        return internalSpark.follow(leader, invert);
    }

    @Override
    public CANError follow(ExternalFollower leader, int deviceID) {
        return internalSpark.follow(leader, deviceID);
    }

    @Override
    public CANError follow(ExternalFollower leader, int deviceID, boolean invert) {
        return internalSpark.follow(leader, deviceID, invert);
    }

    @Override
    public boolean isFollower() {
        return internalSpark.isFollower();
    }

    @Override
    public short getFaults() {
        return internalSpark.getFaults();
    }

    @Override
    public short getStickyFaults() {
        return internalSpark.getStickyFaults();
    }

    @Override
    public boolean getFault(FaultID faultID) {
        return internalSpark.getFault(faultID);
    }

    @Override
    public boolean getStickyFault(FaultID faultID) {
        return internalSpark.getStickyFault(faultID);
    }

    @Override
    public double getBusVoltage() {
        return internalSpark.getBusVoltage();
    }

    @Override
    public double getAppliedOutput() {
        return internalSpark.getAppliedOutput();
    }

    @Override
    public double getOutputCurrent() {
        return internalSpark.getOutputCurrent();
    }

    @Override
    public double getMotorTemperature() {
        return internalSpark.getMotorTemperature();
    }

    @Override
    public CANError clearFaults() {
        return internalSpark.clearFaults();
    }

    @Override
    public CANError burnFlash() {
        return internalSpark.burnFlash();
    }

    @Override
    public CANError setCANTimeout(int milliseconds) {
        return internalSpark.setCANTimeout(milliseconds);
    }

    @Override
    public CANError enableSoftLimit(SoftLimitDirection direction, boolean enable) {
        return internalSpark.enableSoftLimit(direction, enable);
    }

    @Override
    public CANError setSoftLimit(SoftLimitDirection direction, float limit) {
        return internalSpark.setSoftLimit(direction, limit);
    }

    @Override
    public double getSoftLimit(SoftLimitDirection direction) {
        return internalSpark.getSoftLimit(direction);
    }

    @Override
    public boolean isSoftLimitEnabled(SoftLimitDirection direction) {
        return internalSpark.isSoftLimitEnabled(direction);
    }

    @Override
    public CANError getLastError() {
        return internalSpark.getLastError();
    }
}