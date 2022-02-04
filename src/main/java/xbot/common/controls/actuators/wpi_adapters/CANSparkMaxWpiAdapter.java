package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.revrobotics.CANAnalog;
import com.revrobotics.CANAnalog.AnalogMode;
import com.revrobotics.CANDigitalInput;
import com.revrobotics.CANDigitalInput.LimitSwitchPolarity;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANPIDController.AccelStrategy;
import com.revrobotics.CANPIDController.ArbFFUnits;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.ControlType;
import com.revrobotics.REVLibError;
import com.revrobotics.RelativeEncoder;

import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.wpi_factories.CommonLibFactory;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;

public class CANSparkMaxWpiAdapter extends XCANSparkMax {

    private CANSparkMax internalSpark;

    @Inject
    public CANSparkMaxWpiAdapter(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix, @Assisted("name") String name,
            PropertyFactory propMan, DevicePolice police, CommonLibFactory clf) {
        super(deviceInfo, owningSystemPrefix, name, propMan, police, clf);
        internalSpark = new CANSparkMax(deviceInfo.channel, MotorType.kBrushless);
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
    public REVLibError setSmartCurrentLimit(int limit) {
        return internalSpark.setSmartCurrentLimit(limit);
    }

    @Override
    public REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit) {
        return internalSpark.setSmartCurrentLimit(stallLimit, freeLimit);
    }

    @Override
    public REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM) {
        return internalSpark.setSmartCurrentLimit(stallLimit, freeLimit, limitRPM);
    }

    @Override
    public REVLibError setSecondaryCurrentLimit(double limit) {
        return internalSpark.setSecondaryCurrentLimit(limit);
    }

    @Override
    public REVLibError setSecondaryCurrentLimit(double limit, int chopCycles) {
        return internalSpark.setSecondaryCurrentLimit(limit, chopCycles);
    }

    @Override
    public REVLibError setIdleMode(IdleMode mode) {
        return internalSpark.setIdleMode(mode);
    }

    @Override
    public IdleMode getIdleMode() {
        return internalSpark.getIdleMode();
    }

    @Override
    public REVLibError enableVoltageCompensation(double nominalVoltage) {
        return internalSpark.enableVoltageCompensation(nominalVoltage);
    }

    @Override
    public REVLibError disableVoltageCompensation() {
        return internalSpark.disableVoltageCompensation();
    }

    @Override
    public double getVoltageCompensationNominalVoltage() {
        return internalSpark.getVoltageCompensationNominalVoltage();
    }

    @Override
    public REVLibError setOpenLoopRampRate(double rate) {
        return internalSpark.setOpenLoopRampRate(rate);
    }

    @Override
    public REVLibError setClosedLoopRampRate(double rate) {
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
    public REVLibError follow(XCANSparkMax leader) {
        return internalSpark.follow(leader.getInternalSparkMax());
    }

    @Override
    public REVLibError follow(XCANSparkMax leader, boolean invert) {
        return internalSpark.follow(leader.getInternalSparkMax(), invert);
    }

    @Override
    public REVLibError follow(ExternalFollower leader, int deviceID) {
        return internalSpark.follow(leader, deviceID);
    }

    @Override
    public REVLibError follow(ExternalFollower leader, int deviceID, boolean invert) {
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
    public REVLibError clearFaults() {
        return internalSpark.clearFaults();
    }

    @Override
    public REVLibError burnFlash() {
        return internalSpark.burnFlash();
    }

    @Override
    public REVLibError setCANTimeout(int milliseconds) {
        return internalSpark.setCANTimeout(milliseconds);
    }

    @Override
    public REVLibError enableSoftLimit(SoftLimitDirection direction, boolean enable) {
        return internalSpark.enableSoftLimit(direction, enable);
    }

    @Override
    public REVLibError setSoftLimit(SoftLimitDirection direction, float limit) {
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
    public REVLibError getLastError() {
        return internalSpark.getLastError();
    }

    @Override
    public REVLibError restoreFactoryDefaults() {
        return internalSpark.restoreFactoryDefaults();
    }

    RelativeEncoder ce;
    private RelativeEncoder getEncoderInstance() {
        if (ce == null) {
            ce = internalSpark.getEncoder();
        }
        return ce;
    }

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

    public REVLibError setMeasurementPeriod(int period_us) {
        return getEncoderInstance().setMeasurementPeriod(period_us);
    }

    public int getMeasurementPeriod() {
        return getEncoderInstance().getMeasurementPeriod();
    }

    public int getCountsPerRevolution() {
        return getEncoderInstance().getCountsPerRevolution();
    }

    public int hashCode() {
        return getEncoderInstance().hashCode();
    }

    public REVLibError setEncoderInverted(boolean inverted) {
        return getEncoderInstance().setInverted(inverted);
    }

    private CANPIDController cpc;
    private CANPIDController getCANPIDControllerInstance() {
        if (cpc == null) {
            cpc = internalSpark.getPIDController();
        }
        return cpc;
    }

    public REVLibError setReference(double value, ControlType ctrl) {
        return getCANPIDControllerInstance().setReference(value, ctrl);
    }

    public REVLibError setReference(double value, ControlType ctrl, int pidSlot) {
        return getCANPIDControllerInstance().setReference(value, ctrl, pidSlot);
    }

    public REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward) {
        return getCANPIDControllerInstance().setReference(value, ctrl, pidSlot, arbFeedforward);
    }

    public REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward,
            ArbFFUnits arbFFUnits) {
        return getCANPIDControllerInstance().setReference(value, ctrl, pidSlot, arbFeedforward, arbFFUnits);
    }

    public REVLibError setP(double gain) {
        return getCANPIDControllerInstance().setP(gain);
    }

    public REVLibError setP(double gain, int slotID) {
        return getCANPIDControllerInstance().setP(gain, slotID);
    }

    public REVLibError setI(double gain) {
        return getCANPIDControllerInstance().setI(gain);
    }

    public REVLibError setI(double gain, int slotID) {
        return getCANPIDControllerInstance().setI(gain, slotID);
    }

    public REVLibError setD(double gain) {
        return getCANPIDControllerInstance().setD(gain);
    }

    public REVLibError setD(double gain, int slotID) {
        return getCANPIDControllerInstance().setD(gain, slotID);
    }

    public REVLibError setDFilter(double gain) {
        return getCANPIDControllerInstance().setDFilter(gain);
    }

    public REVLibError setDFilter(double gain, int slotID) {
        return getCANPIDControllerInstance().setDFilter(gain, slotID);
    }

    public REVLibError setFF(double gain) {
        return getCANPIDControllerInstance().setFF(gain);
    }

    public REVLibError setFF(double gain, int slotID) {
        return getCANPIDControllerInstance().setFF(gain, slotID);
    }

    //CHECKSTYLE:OFF
    public REVLibError setIZone(double IZone) {
        return getCANPIDControllerInstance().setIZone(IZone);
    }

    public REVLibError setIZone(double IZone, int slotID) {
        return getCANPIDControllerInstance().setIZone(IZone, slotID);
    }
    //CHECKSTYLE:ON

    public REVLibError setOutputRange(double min, double max) {
        return getCANPIDControllerInstance().setOutputRange(min, max);
    }

    public REVLibError setOutputRange(double min, double max, int slotID) {
        return getCANPIDControllerInstance().setOutputRange(min, max, slotID);
    }

    public double getP() {
        return getCANPIDControllerInstance().getP();
    }

    public double getP(int slotID) {
        return getCANPIDControllerInstance().getP(slotID);
    }

    public double getI() {
        return getCANPIDControllerInstance().getI();
    }

    public double getI(int slotID) {
        return getCANPIDControllerInstance().getI(slotID);
    }

    public double getD() {
        return getCANPIDControllerInstance().getD();
    }

    public double getD(int slotID) {
        return getCANPIDControllerInstance().getD(slotID);
    }

    public double getDFilter(int slotID) {
        return getCANPIDControllerInstance().getDFilter(slotID);
    }

    public double getFF() {
        return getCANPIDControllerInstance().getFF();
    }

    public double getFF(int slotID) {
        return getCANPIDControllerInstance().getFF(slotID);
    }

    public double getIZone() {
        return getCANPIDControllerInstance().getIZone();
    }

    public double getIZone(int slotID) {
        return getCANPIDControllerInstance().getIZone(slotID);
    }

    public double getOutputMin() {
        return getCANPIDControllerInstance().getOutputMin();
    }

    public double getOutputMin(int slotID) {
        return getCANPIDControllerInstance().getOutputMin(slotID);
    }

    public double getOutputMax() {
        return getCANPIDControllerInstance().getOutputMax();
    }

    public double getOutputMax(int slotID) {
        return getCANPIDControllerInstance().getOutputMax(slotID);
    }

    public REVLibError setSmartMotionMaxVelocity(double maxVel, int slotID) {
        return getCANPIDControllerInstance().setSmartMotionMaxVelocity(maxVel, slotID);
    }

    public REVLibError setSmartMotionMaxAccel(double maxAccel, int slotID) {
        return getCANPIDControllerInstance().setSmartMotionMaxAccel(maxAccel, slotID);
    }

    public REVLibError setSmartMotionMinOutputVelocity(double minVel, int slotID) {
        return getCANPIDControllerInstance().setSmartMotionMinOutputVelocity(minVel, slotID);
    }

    public REVLibError setSmartMotionAllowedClosedLoopError(double allowedErr, int slotID) {
        return getCANPIDControllerInstance().setSmartMotionAllowedClosedLoopError(allowedErr, slotID);
    }

    public REVLibError setSmartMotionAccelStrategy(AccelStrategy accelStrategy, int slotID) {
        return getCANPIDControllerInstance().setSmartMotionAccelStrategy(accelStrategy, slotID);
    }

    public double getSmartMotionMaxVelocity(int slotID) {
        return getCANPIDControllerInstance().getSmartMotionMaxVelocity(slotID);
    }

    public double getSmartMotionMaxAccel(int slotID) {
        return getCANPIDControllerInstance().getSmartMotionMaxAccel(slotID);
    }

    public double getSmartMotionMinOutputVelocity(int slotID) {
        return getCANPIDControllerInstance().getSmartMotionMinOutputVelocity(slotID);
    }

    public double getSmartMotionAllowedClosedLoopError(int slotID) {
        return getCANPIDControllerInstance().getSmartMotionAllowedClosedLoopError(slotID);
    }

    public REVLibError setIMaxAccum(double iMaxAccum, int slotID) {
        return getCANPIDControllerInstance().setIMaxAccum(iMaxAccum, slotID);
    }

    public double getIMaxAccum(int slotID) {
        return getCANPIDControllerInstance().getIMaxAccum(slotID);
    }

    public REVLibError setIAccum(double iAccum) {
        return getCANPIDControllerInstance().setIAccum(iAccum);
    }

    public double getIAccum() {
        return getCANPIDControllerInstance().getIAccum();
    }

    @Override
    public CANSparkMax getInternalSparkMax() {
        return internalSpark;
    }
}