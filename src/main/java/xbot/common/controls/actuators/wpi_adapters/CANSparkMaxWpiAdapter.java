package xbot.common.controls.actuators.wpi_adapters;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;
import com.revrobotics.MotorFeedbackSensor;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxAnalogSensor;
import com.revrobotics.SparkMaxAnalogSensor.Mode;
import com.revrobotics.SparkMaxLimitSwitch;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.SparkMaxPIDController.AccelStrategy;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;
import com.revrobotics.SparkMaxRelativeEncoder.Type;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.properties.PropertyFactory;

public class CANSparkMaxWpiAdapter extends XCANSparkMax {

    private CANSparkMax internalSpark;

    @AssistedFactory
    public abstract static class CANSparkMaxWpiAdapterFactory extends XCANSparkMaxFactory {
        public abstract CANSparkMaxWpiAdapter create(
            @Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            @Assisted("name") String name,
            @Assisted("defaultPIDProperties") XCANSparkMaxPIDProperties defaultPIDProperties);
    }

    @AssistedInject
    public CANSparkMaxWpiAdapter(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix, @Assisted("name") String name,
            PropertyFactory propMan, DevicePolice police,
            @Assisted("defaultPIDProperties") XCANSparkMaxPIDProperties defaultPIDProperties) {
        super(deviceInfo, owningSystemPrefix, name, propMan, police, defaultPIDProperties);
        internalSpark = new CANSparkMax(deviceInfo.channel, MotorType.kBrushless);
        setInverted(deviceInfo.inverted);
    }

    @Override
    public CANSparkMax getInternalSparkMax() {
        return internalSpark;
    }

    public void close() {
        internalSpark.close();
    }

    @Override
    public REVLibError follow(XCANSparkMax leader) {
        return internalSpark.follow(leader.getInternalSparkMax());
    }

    @Override
    public REVLibError follow(XCANSparkMax leader, boolean invert) {
        return internalSpark.follow(leader.getInternalSparkMax(), invert);
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

    public REVLibError setP(double gain) {
        return getPIDControllerInstance().setP(gain);
    }

    public REVLibError setP(double gain, int slotID) {
        return getPIDControllerInstance().setP(gain, slotID);
    }

    public REVLibError setI(double gain) {
        return getPIDControllerInstance().setI(gain);
    }

    public REVLibError setI(double gain, int slotID) {
        return getPIDControllerInstance().setI(gain, slotID);
    }

    public REVLibError setD(double gain) {
        return getPIDControllerInstance().setD(gain);
    }

    public REVLibError setD(double gain, int slotID) {
        return getPIDControllerInstance().setD(gain, slotID);
    }

    public REVLibError setDFilter(double gain) {
        return getPIDControllerInstance().setDFilter(gain);
    }

    public REVLibError setDFilter(double gain, int slotID) {
        return getPIDControllerInstance().setDFilter(gain, slotID);
    }

    public REVLibError setFF(double gain) {
        return getPIDControllerInstance().setFF(gain);
    }

    public REVLibError setFF(double gain, int slotID) {
        return getPIDControllerInstance().setFF(gain, slotID);
    }

    public REVLibError setIZone(double iZone) {
        return getPIDControllerInstance().setIZone(iZone);
    }

    public REVLibError setIZone(double iZone, int slotID) {
        return getPIDControllerInstance().setIZone(iZone, slotID);
    }

    public REVLibError setOutputRange(double min, double max) {
        return getPIDControllerInstance().setOutputRange(min, max);
    }

    public REVLibError setOutputRange(double min, double max, int slotID) {
        return getPIDControllerInstance().setOutputRange(min, max, slotID);
    }

    public double getP() {
        return getPIDControllerInstance().getP();
    }

    public double getP(int slotID) {
        return getPIDControllerInstance().getP(slotID);
    }

    public double getI() {
        return getPIDControllerInstance().getI();
    }

    public double getI(int slotID) {
        return getPIDControllerInstance().getI(slotID);
    }

    public double getD() {
        return getPIDControllerInstance().getD();
    }

    public double getD(int slotID) {
        return getPIDControllerInstance().getD(slotID);
    }

    public double getDFilter(int slotID) {
        return getPIDControllerInstance().getDFilter(slotID);
    }

    public double getFF() {
        return getPIDControllerInstance().getFF();
    }

    public double getFF(int slotID) {
        return getPIDControllerInstance().getFF(slotID);
    }

    public double getIZone() {
        return getPIDControllerInstance().getIZone();
    }

    public double getIZone(int slotID) {
        return getPIDControllerInstance().getIZone(slotID);
    }

    public double getOutputMin() {
        return getPIDControllerInstance().getOutputMin();
    }

    public double getOutputMin(int slotID) {
        return getPIDControllerInstance().getOutputMin(slotID);
    }

    public double getOutputMax() {
        return getPIDControllerInstance().getOutputMax();
    }

    public double getOutputMax(int slotID) {
        return getPIDControllerInstance().getOutputMax(slotID);
    }

    public REVLibError setSmartMotionMaxVelocity(double maxVel, int slotID) {
        return getPIDControllerInstance().setSmartMotionMaxVelocity(maxVel, slotID);
    }

    public REVLibError setSmartMotionMaxAccel(double maxAccel, int slotID) {
        return getPIDControllerInstance().setSmartMotionMaxAccel(maxAccel, slotID);
    }

    public REVLibError setSmartMotionMinOutputVelocity(double minVel, int slotID) {
        return getPIDControllerInstance().setSmartMotionMinOutputVelocity(minVel, slotID);
    }

    public REVLibError setSmartMotionAllowedClosedLoopError(double allowedErr, int slotID) {
        return getPIDControllerInstance().setSmartMotionAllowedClosedLoopError(allowedErr, slotID);
    }

    public REVLibError setSmartMotionAccelStrategy(AccelStrategy accelStrategy, int slotID) {
        return getPIDControllerInstance().setSmartMotionAccelStrategy(accelStrategy, slotID);
    }

    public double getSmartMotionMaxVelocity(int slotID) {
        return getPIDControllerInstance().getSmartMotionMaxVelocity(slotID);
    }

    public double getSmartMotionMaxAccel(int slotID) {
        return getPIDControllerInstance().getSmartMotionMaxAccel(slotID);
    }

    public double getSmartMotionMinOutputVelocity(int slotID) {
        return getPIDControllerInstance().getSmartMotionMinOutputVelocity(slotID);
    }

    public double getSmartMotionAllowedClosedLoopError(int slotID) {
        return getPIDControllerInstance().getSmartMotionAllowedClosedLoopError(slotID);
    }

    public AccelStrategy getSmartMotionAccelStrategy(int slotID) {
        return getPIDControllerInstance().getSmartMotionAccelStrategy(slotID);
    }

    public REVLibError setIMaxAccum(double iMaxAccum, int slotID) {
        return getPIDControllerInstance().setIMaxAccum(iMaxAccum, slotID);
    }

    public double getIMaxAccum(int slotID) {
        return getPIDControllerInstance().getIMaxAccum(slotID);
    }

    public REVLibError setIAccum(double iAccum) {
        return getPIDControllerInstance().setIAccum(iAccum);
    }

    public double getIAccum() {
        return getPIDControllerInstance().getIAccum();
    }

    public REVLibError setFeedbackDevice(MotorFeedbackSensor sensor) {
        return getPIDControllerInstance().setFeedbackDevice(sensor);
    }

    public REVLibError setReference(double value, ControlType ctrl) {
        return getPIDControllerInstance().setReference(value, ctrl);
    }

    public REVLibError setReference(double value, ControlType ctrl, int pidSlot) {
        return getPIDControllerInstance().setReference(value, ctrl, pidSlot);
    }

    public REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward) {
        return getPIDControllerInstance().setReference(value, ctrl, pidSlot, arbFeedforward);
    }

    public REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward,
            ArbFFUnits arbFFUnits) {
        return getPIDControllerInstance().setReference(value, ctrl, pidSlot, arbFeedforward, arbFFUnits);
    }

    public String toString() {
        return getPIDControllerInstance().toString();
    }

    @Override
    public void setForwardLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type switchType, boolean enabled) {
        internalSpark.getForwardLimitSwitch(switchType).enableLimitSwitch(enabled);
    }

    @Override
    public void setReverseLimitSwitch(com.revrobotics.SparkMaxLimitSwitch.Type switchType, boolean enabled) {
        internalSpark.getReverseLimitSwitch(switchType).enableLimitSwitch(enabled);
    }

    @Override
    public boolean getForwardLimitSwitchPressed(com.revrobotics.SparkMaxLimitSwitch.Type switchType) {
        return internalSpark.getForwardLimitSwitch(switchType).isPressed();
    }

    @Override
    public boolean getReverseLimitSwitchPressed(com.revrobotics.SparkMaxLimitSwitch.Type switchType) {
        return internalSpark.getReverseLimitSwitch(switchType).isPressed();
    }
}