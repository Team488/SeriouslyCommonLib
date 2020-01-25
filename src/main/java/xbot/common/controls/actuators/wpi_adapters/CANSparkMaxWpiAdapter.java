package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.revrobotics.CANAnalog;
import com.revrobotics.CANAnalog.AnalogMode;
import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANDigitalInput.LimitSwitchPolarity;
import com.revrobotics.CANError;
import com.revrobotics.CANPIDController.AccelStrategy;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;

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
    public CANAnalog getAnalog(AnalogMode mode) {
        return internalSpark.getAnalog(mode);
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

    @Override
    public CANError restoreFactoryDefaults() {
        return internalSpark.restoreFactoryDefaults();
    }

    public double getPosition() {
        return internalSpark.getEncoder().getPosition();
    }

    public double getVelocity() {
        return internalSpark.getEncoder().getVelocity();
    }

    public CANError setPosition(double position) {
        return internalSpark.getEncoder().setPosition(position);
    }

    public CANError setPositionConversionFactor(double factor) {
        return internalSpark.getEncoder().setPositionConversionFactor(factor);
    }

    public CANError setVelocityConversionFactor(double factor) {
        return internalSpark.getEncoder().setVelocityConversionFactor(factor);
    }

    public double getPositionConversionFactor() {
        return internalSpark.getEncoder().getPositionConversionFactor();
    }

    public double getVelocityConversionFactor() {
        return internalSpark.getEncoder().getVelocityConversionFactor();
    }

    public CANError setAverageDepth(int depth) {
        return internalSpark.getEncoder().setAverageDepth(depth);
    }

    public int getAverageDepth() {
        return internalSpark.getEncoder().getAverageDepth();
    }

    public CANError setMeasurementPeriod(int period_us) {
        return internalSpark.getEncoder().setMeasurementPeriod(period_us);
    }

    public int getMeasurementPeriod() {
        return internalSpark.getEncoder().getMeasurementPeriod();
    }

    @Deprecated
    public int getCPR() {
        return internalSpark.getEncoder().getCPR();
    }

    public int getCountsPerRevolution() {
        return internalSpark.getEncoder().getCountsPerRevolution();
    }

    public int hashCode() {
        return internalSpark.getEncoder().hashCode();
    }

    public CANError setEncoderInverted(boolean inverted) {
        return internalSpark.getEncoder().setInverted(inverted);
    }

    public String toString() {
        return internalSpark.getEncoder().toString();
    }

    public CANError setReference(double value, ControlType ctrl) {
        return internalSpark.getPIDController().setReference(value, ctrl);
    }

    public CANError setReference(double value, ControlType ctrl, int pidSlot) {
        return internalSpark.getPIDController().setReference(value, ctrl, pidSlot);
    }

    public CANError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward) {
        return internalSpark.getPIDController().setReference(value, ctrl, pidSlot, arbFeedforward);
    }

    public CANError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward,
            ArbFFUnits arbFFUnits) {
        return internalSpark.getPIDController().setReference(value, ctrl, pidSlot, arbFeedforward, arbFFUnits);
    }

    public CANError setP(double gain) {
        return internalSpark.getPIDController().setP(gain);
    }

    public CANError setP(double gain, int slotID) {
        return internalSpark.getPIDController().setP(gain, slotID);
    }

    public CANError setI(double gain) {
        return internalSpark.getPIDController().setI(gain);
    }

    public CANError setI(double gain, int slotID) {
        return internalSpark.getPIDController().setI(gain, slotID);
    }

    public CANError setD(double gain) {
        return internalSpark.getPIDController().setD(gain);
    }

    public CANError setD(double gain, int slotID) {
        return internalSpark.getPIDController().setD(gain, slotID);
    }

    public CANError setDFilter(double gain) {
        return internalSpark.getPIDController().setDFilter(gain);
    }

    public CANError setDFilter(double gain, int slotID) {
        return internalSpark.getPIDController().setDFilter(gain, slotID);
    }

    public CANError setFF(double gain) {
        return internalSpark.getPIDController().setFF(gain);
    }

    public CANError setFF(double gain, int slotID) {
        return internalSpark.getPIDController().setFF(gain, slotID);
    }

    public CANError setIZone(double IZone) {
        return internalSpark.getPIDController().setIZone(IZone);
    }

    public CANError setIZone(double IZone, int slotID) {
        return internalSpark.getPIDController().setIZone(IZone, slotID);
    }

    public CANError setOutputRange(double min, double max) {
        return internalSpark.getPIDController().setOutputRange(min, max);
    }

    public CANError setOutputRange(double min, double max, int slotID) {
        return internalSpark.getPIDController().setOutputRange(min, max, slotID);
    }

    public double getP() {
        return internalSpark.getPIDController().getP();
    }

    public double getP(int slotID) {
        return internalSpark.getPIDController().getP(slotID);
    }

    public double getI() {
        return internalSpark.getPIDController().getI();
    }

    public double getI(int slotID) {
        return internalSpark.getPIDController().getI(slotID);
    }

    public double getD() {
        return internalSpark.getPIDController().getD();
    }

    public double getD(int slotID) {
        return internalSpark.getPIDController().getD(slotID);
    }

    public double getDFilter(int slotID) {
        return internalSpark.getPIDController().getDFilter(slotID);
    }

    public double getFF() {
        return internalSpark.getPIDController().getFF();
    }

    public double getFF(int slotID) {
        return internalSpark.getPIDController().getFF(slotID);
    }

    public double getIZone() {
        return internalSpark.getPIDController().getIZone();
    }

    public double getIZone(int slotID) {
        return internalSpark.getPIDController().getIZone(slotID);
    }

    public double getOutputMin() {
        return internalSpark.getPIDController().getOutputMin();
    }

    public double getOutputMin(int slotID) {
        return internalSpark.getPIDController().getOutputMin(slotID);
    }

    public double getOutputMax() {
        return internalSpark.getPIDController().getOutputMax();
    }

    public double getOutputMax(int slotID) {
        return internalSpark.getPIDController().getOutputMax(slotID);
    }

    public CANError setSmartMotionMaxVelocity(double maxVel, int slotID) {
        return internalSpark.getPIDController().setSmartMotionMaxVelocity(maxVel, slotID);
    }

    public CANError setSmartMotionMaxAccel(double maxAccel, int slotID) {
        return internalSpark.getPIDController().setSmartMotionMaxAccel(maxAccel, slotID);
    }

    public CANError setSmartMotionMinOutputVelocity(double minVel, int slotID) {
        return internalSpark.getPIDController().setSmartMotionMinOutputVelocity(minVel, slotID);
    }

    public CANError setSmartMotionAllowedClosedLoopError(double allowedErr, int slotID) {
        return internalSpark.getPIDController().setSmartMotionAllowedClosedLoopError(allowedErr, slotID);
    }

    public CANError setSmartMotionAccelStrategy(AccelStrategy accelStrategy, int slotID) {
        return internalSpark.getPIDController().setSmartMotionAccelStrategy(accelStrategy, slotID);
    }

    public double getSmartMotionMaxVelocity(int slotID) {
        return internalSpark.getPIDController().getSmartMotionMaxVelocity(slotID);
    }

    public double getSmartMotionMaxAccel(int slotID) {
        return internalSpark.getPIDController().getSmartMotionMaxAccel(slotID);
    }

    public double getSmartMotionMinOutputVelocity(int slotID) {
        return internalSpark.getPIDController().getSmartMotionMinOutputVelocity(slotID);
    }

    public double getSmartMotionAllowedClosedLoopError(int slotID) {
        return internalSpark.getPIDController().getSmartMotionAllowedClosedLoopError(slotID);
    }

    public AccelStrategy getSmartMotionAccelStrategy(int slotID) {
        return internalSpark.getPIDController().getSmartMotionAccelStrategy(slotID);
    }

    public CANError setIMaxAccum(double iMaxAccum, int slotID) {
        return internalSpark.getPIDController().setIMaxAccum(iMaxAccum, slotID);
    }

    public double getIMaxAccum(int slotID) {
        return internalSpark.getPIDController().getIMaxAccum(slotID);
    }

    public CANError setIAccum(double iAccum) {
        return internalSpark.getPIDController().setIAccum(iAccum);
    }

    public double getIAccum() {
        return internalSpark.getPIDController().getIAccum();
    }
}