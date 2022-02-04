package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.revrobotics.SparkMaxAnalogSensor.Mode;
import com.revrobotics.SparkMaxLimitSwitch;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxRelativeEncoder.Type;

import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class CANSparkMaxWpiAdapter extends XCANSparkMax {

    private CANSparkMax internalSpark;

    @Inject
    public CANSparkMaxWpiAdapter(@Assisted("deviceId") int deviceId,
            @Assisted("owningSystemPrefix") String owningSystemPrefix, @Assisted("name") String name,
            PropertyFactory propMan, DevicePolice police, CommonLibFactory clf) {
        super(deviceId, owningSystemPrefix, name, propMan, police, clf);
        internalSpark = new CANSparkMax(deviceId, MotorType.kBrushless);
    }

    @Override
    public CANSparkMax getInternalSparkMax() {
        return internalSpark;
    }

    public void close() {
        internalSpark.close();
    }

    public boolean equals(Object obj) {
        return internalSpark.equals(obj);
    }

    public int getFirmwareVersion() {
        return internalSpark.getFirmwareVersion();
    }

    public void setControlFramePeriodMs(int periodMs) {
        internalSpark.setControlFramePeriodMs(periodMs);
    }

    public String getFirmwareString() {
        return internalSpark.getFirmwareString();
    }

    public byte[] getSerialNumber() {
        return internalSpark.getSerialNumber();
    }

    public int getDeviceId() {
        return internalSpark.getDeviceId();
    }

    public void stopMotor() {
        internalSpark.stopMotor();
    }

    public MotorType getMotorType() {
        return internalSpark.getMotorType();
    }

    public REVLibError setPeriodicFramePeriod(PeriodicFrame frame, int periodMs) {
        return internalSpark.setPeriodicFramePeriod(frame, periodMs);
    }

    public float getSafeFloat(float f) {
        return internalSpark.getSafeFloat(f);
    }

    public int hashCode() {
        return internalSpark.hashCode();
    }

    public void set(double speed) {
        internalSpark.set(speed);
    }

    public void setVoltage(double outputVolts) {
        internalSpark.setVoltage(outputVolts);
    }

    public double get() {
        return internalSpark.get();
    }

    public void setInverted(boolean isInverted) {
        internalSpark.setInverted(isInverted);
    }

    public boolean getInverted() {
        return internalSpark.getInverted();
    }

    public void disable() {
        internalSpark.disable();
    }

    public RelativeEncoder getEncoder(Type encoderType, int countsPerRev) {
        return internalSpark.getEncoder(encoderType, countsPerRev);
    }

    public RelativeEncoder getAlternateEncoder(int countsPerRev) {
        return internalSpark.getAlternateEncoder(countsPerRev);
    }

    public RelativeEncoder getAlternateEncoder(com.revrobotics.SparkMaxAlternateEncoder.Type encoderType,
            int countsPerRev) {
        return internalSpark.getAlternateEncoder(encoderType, countsPerRev);
    }

    public REVLibError restoreFactoryDefaults() {
        return internalSpark.restoreFactoryDefaults();
    }

    public REVLibError restoreFactoryDefaults(boolean persist) {
        return internalSpark.restoreFactoryDefaults(persist);
    }

    public SparkMaxAnalogSensor getAnalog(Mode mode) {
        return internalSpark.getAnalog(mode);
    }

    public SparkMaxPIDController getPIDController() {
        return internalSpark.getPIDController();
    }

    public SparkMaxLimitSwitch getForwardLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type switchType) {
        return internalSpark.getForwardLimitSwitch(switchType);
    }

    public SparkMaxLimitSwitch getReverseLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type switchType) {
        return internalSpark.getReverseLimitSwitch(switchType);
    }

    public REVLibError setSmartCurrentLimit(int limit) {
        return internalSpark.setSmartCurrentLimit(limit);
    }

    public REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit) {
        return internalSpark.setSmartCurrentLimit(stallLimit, freeLimit);
    }

    public REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM) {
        return internalSpark.setSmartCurrentLimit(stallLimit, freeLimit, limitRPM);
    }

    public REVLibError setSecondaryCurrentLimit(double limit) {
        return internalSpark.setSecondaryCurrentLimit(limit);
    }

    public REVLibError setSecondaryCurrentLimit(double limit, int chopCycles) {
        return internalSpark.setSecondaryCurrentLimit(limit, chopCycles);
    }

    public REVLibError setIdleMode(IdleMode mode) {
        return internalSpark.setIdleMode(mode);
    }

    public IdleMode getIdleMode() {
        return internalSpark.getIdleMode();
    }

    public REVLibError enableVoltageCompensation(double nominalVoltage) {
        return internalSpark.enableVoltageCompensation(nominalVoltage);
    }

    public REVLibError disableVoltageCompensation() {
        return internalSpark.disableVoltageCompensation();
    }

    public double getVoltageCompensationNominalVoltage() {
        return internalSpark.getVoltageCompensationNominalVoltage();
    }

    public REVLibError setOpenLoopRampRate(double rate) {
        return internalSpark.setOpenLoopRampRate(rate);
    }

    public REVLibError setClosedLoopRampRate(double rate) {
        return internalSpark.setClosedLoopRampRate(rate);
    }

    public double getOpenLoopRampRate() {
        return internalSpark.getOpenLoopRampRate();
    }

    public double getClosedLoopRampRate() {
        return internalSpark.getClosedLoopRampRate();
    }

    public REVLibError follow(CANSparkMax leader) {
        return internalSpark.follow(leader);
    }

    public REVLibError follow(CANSparkMax leader, boolean invert) {
        return internalSpark.follow(leader, invert);
    }

    public REVLibError follow(ExternalFollower leader, int deviceID) {
        return internalSpark.follow(leader, deviceID);
    }

    public REVLibError follow(ExternalFollower leader, int deviceID, boolean invert) {
        return internalSpark.follow(leader, deviceID, invert);
    }

    public boolean isFollower() {
        return internalSpark.isFollower();
    }

    public short getFaults() {
        return internalSpark.getFaults();
    }

    public short getStickyFaults() {
        return internalSpark.getStickyFaults();
    }

    public boolean getFault(FaultID faultID) {
        return internalSpark.getFault(faultID);
    }

    public boolean getStickyFault(FaultID faultID) {
        return internalSpark.getStickyFault(faultID);
    }

    public double getBusVoltage() {
        return internalSpark.getBusVoltage();
    }

    public double getAppliedOutput() {
        return internalSpark.getAppliedOutput();
    }

    public double getOutputCurrent() {
        return internalSpark.getOutputCurrent();
    }

    public double getMotorTemperature() {
        return internalSpark.getMotorTemperature();
    }

    public REVLibError clearFaults() {
        return internalSpark.clearFaults();
    }

    public REVLibError burnFlash() {
        return internalSpark.burnFlash();
    }

    public REVLibError setCANTimeout(int milliseconds) {
        return internalSpark.setCANTimeout(milliseconds);
    }

    public REVLibError enableSoftLimit(SoftLimitDirection direction, boolean enable) {
        return internalSpark.enableSoftLimit(direction, enable);
    }

    public REVLibError setSoftLimit(SoftLimitDirection direction, float limit) {
        return internalSpark.setSoftLimit(direction, limit);
    }

    public double getSoftLimit(SoftLimitDirection direction) {
        return internalSpark.getSoftLimit(direction);
    }

    public boolean isSoftLimitEnabled(SoftLimitDirection direction) {
        return internalSpark.isSoftLimitEnabled(direction);
    }

    public REVLibError getLastError() {
        return internalSpark.getLastError();
    }

    RelativeEncoder ce;
    public double getPosition() {
        return getEncoderInstance().getPosition();
    }

    public double getVelocity() {
        return getEncoderInstance().getVelocity();
    }

    public REVLibError setPosition(double position) {
        return getEncoderInstance().setPosition(position);
    }

    public REVLibError setPositionConversionFactor(double factor) {
        return getEncoderInstance().setPositionConversionFactor(factor);
    }

    public REVLibError setVelocityConversionFactor(double factor) {
        return getEncoderInstance().setVelocityConversionFactor(factor);
    }

    public double getPositionConversionFactor() {
        return getEncoderInstance().getPositionConversionFactor();
    }

    public double getVelocityConversionFactor() {
        return getEncoderInstance().getVelocityConversionFactor();
    }

    public REVLibError setAverageDepth(int depth) {
        return getEncoderInstance().setAverageDepth(depth);
    }

    public int getAverageDepth() {
        return getEncoderInstance().getAverageDepth();
    }

    public REVLibError setMeasurementPeriod(int period_ms) {
        return getEncoderInstance().setMeasurementPeriod(period_ms);
    }

    public int getMeasurementPeriod() {
        return getEncoderInstance().getMeasurementPeriod();
    }

    public int getCountsPerRevolution() {
        return getEncoderInstance().getCountsPerRevolution();
    }

    public REVLibError setEncoderInverted(boolean inverted) {
        return getEncoderInstance().setInverted(inverted);
    }

    private RelativeEncoder getEncoderInstance() {
        if (ce == null) {
            ce = internalSpark.getEncoder();
        }
        return ce;
    }    

    SparkMaxPIDController pc;
    private SparkMaxPIDController getPIDControllerInstance() {
        if (pc==null) {
            pc = internalSpark.getPIDController();
        }
        return pc;
    }
}