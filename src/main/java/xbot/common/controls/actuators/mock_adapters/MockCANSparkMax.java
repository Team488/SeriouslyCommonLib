package xbot.common.controls.actuators.mock_adapters;

import java.math.BigDecimal;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.ExternalFollower;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.REVLibError;
import com.revrobotics.SparkMaxLimitSwitch.Type;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.controls.actuators.XCANSparkMaxPIDProperties;
import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.mock_adapters.MockEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.properties.PropertyFactory;
import xbot.common.simulation.ISimulatableMotor;
import xbot.common.simulation.ISimulatableSensor;

public class MockCANSparkMax extends XCANSparkMax implements ISimulatableMotor, ISimulatableSensor {
    private static Logger log = Logger.getLogger(MockCANSparkMax.class);
    private double power = 0;
    private double velocity = 0;
    private double simulationScalingValue;
    boolean inverted = false;
    public XEncoder internalEncoder = null;
    double positionOffset = 0;
    double simulationPosition = 0;

    // PID parameters
    double kP = 0;
    double kI = 0; 
    double kD = 0;
    double kFF = 0;
    double kMinOutput = -1.0;
    double kMaxOutput = 1.0;
    double referenceValue = 0;
    ControlType controlType = null;

    @AssistedFactory
    public abstract static class MockCANSparkMaxFactory extends XCANSparkMaxFactory {
        public abstract MockCANSparkMax create(
            @Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            @Assisted("name") String name,
            @Assisted("defaultPIDProperties") XCANSparkMaxPIDProperties defaultPIDProperties);
    }

    @AssistedInject
    public MockCANSparkMax(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix, @Assisted("name") String name,
            PropertyFactory propMan, DevicePolice police,
            @Assisted("defaultPIDProperties") XCANSparkMaxPIDProperties defaultPIDProperties) {
        super(deviceInfo, owningSystemPrefix, name, propMan, police, defaultPIDProperties);
        log.info("Creating CAN talon with device ID: " + deviceId);
        internalEncoder = new MockEncoder("Test", propMan);
        setInverted(deviceInfo.inverted);

        this.simulationScalingValue = deviceInfo.simulationScalingValue;
        double simulationScalingFloor = 0.00001;
        if (Math.abs(simulationScalingValue) < simulationScalingFloor) {
            log.error("Your scaling value was suspiciously low. Are you sure it should be smaller than "
                    + simulationScalingFloor + "?");
        }
    }

    protected double inversionFactor() {
        return this.getInverted() ? -1 : 1;
    }

    @Override
    public void set(double speed) {
        clearPid();
        power = speed;
    }

    @Override
    public void setVoltage(double outputVolts) {
        clearPid();
        power = outputVolts / 12;
    }

    @Override
    public double get() {
        return power;
    }

    @Override
    public void setInverted(boolean isInverted) {
        inverted = isInverted;
    }

    @Override
    public boolean getInverted() {
        return inverted;
    }

    @Override
    public void disable() {
        clearPid();
        power = 0;
    }

    @Override
    public void stopMotor() {
        clearPid();
        power = 0;
    }

    @Override
    public REVLibError setSmartCurrentLimit(int limit) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSecondaryCurrentLimit(double limit) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSecondaryCurrentLimit(double limit, int chopCycles) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setIdleMode(IdleMode mode) {
        return REVLibError.kOk;
    }

    @Override
    public IdleMode getIdleMode() {
        return null;
    }

    @Override
    public REVLibError enableVoltageCompensation(double nominalVoltage) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError disableVoltageCompensation() {
        return REVLibError.kOk;
    }

    @Override
    public double getVoltageCompensationNominalVoltage() {
        return 0;
    }

    @Override
    public REVLibError setOpenLoopRampRate(double rate) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setClosedLoopRampRate(double rate) {
        return REVLibError.kOk;
    }

    @Override
    public double getOpenLoopRampRate() {
        return 0;
    }

    @Override
    public double getClosedLoopRampRate() {
        return 0;
    }

    @Override
    public REVLibError follow(XCANSparkMax leader) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError follow(XCANSparkMax leader, boolean invert) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError follow(ExternalFollower leader, int deviceID) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError follow(ExternalFollower leader, int deviceID, boolean invert) {
        return REVLibError.kOk;
    }

    @Override
    public boolean isFollower() {
        return false;
    }

    @Override
    public short getFaults() {
        return 0;
    }

    @Override
    public short getStickyFaults() {
        return 0;
    }

    @Override
    public boolean getFault(FaultID faultID) {
        return false;
    }

    @Override
    public boolean getStickyFault(FaultID faultID) {
        return false;
    }

    @Override
    public double getBusVoltage() {
        return 0;
    }

    @Override
    public double getAppliedOutput() {
        return 0;
    }

    @Override
    public double getOutputCurrent() {
        return 0;
    }

    @Override
    public double getMotorTemperature() {
        return 0;
    }

    @Override
    public REVLibError clearFaults() {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError burnFlash() {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setCANTimeout(int milliseconds) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError enableSoftLimit(SoftLimitDirection direction, boolean enable) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSoftLimit(SoftLimitDirection direction, float limit) {
        return REVLibError.kOk;
    }

    @Override
    public double getSoftLimit(SoftLimitDirection direction) {
        return 0;
    }

    @Override
    public boolean isSoftLimitEnabled(SoftLimitDirection direction) {
        return false;
    }

    @Override
    public REVLibError getLastError() {
        return REVLibError.kOk;
    }

    @Override
    public double getPosition() {
        return positionOffset + simulationPosition;
    }

    @Override
    public double getVelocity() {
        return velocity;
    }

    @Override
    public REVLibError setPosition(double position) {
        this.positionOffset = position - simulationPosition;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setPositionConversionFactor(double factor) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setVelocityConversionFactor(double factor) {
        return REVLibError.kOk;
    }

    @Override
    public double getPositionConversionFactor() {
        return 0;
    }

    @Override
    public double getVelocityConversionFactor() {
        return 0;
    }

    @Override
    public REVLibError setAverageDepth(int depth) {
        return REVLibError.kOk;
    }

    @Override
    public int getAverageDepth() {
        return 0;
    }

    @Override
    public REVLibError setMeasurementPeriod(int period_us) {
        return REVLibError.kOk;
    }

    @Override
    public int getMeasurementPeriod() {
        return 0;
    }

    @Override
    public int getCountsPerRevolution() {
        return 0;
    }

    @Override
    public REVLibError setEncoderInverted(boolean inverted) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setP(double gain) {
        kP = gain;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setP(double gain, int slotID) {
        kP = gain;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setI(double gain) {
        kI = gain;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setI(double gain, int slotID) {
        kI = gain;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setD(double gain) {
        kD = gain;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setD(double gain, int slotID) {
        kD = gain;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setDFilter(double gain) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setDFilter(double gain, int slotID) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setFF(double gain) {
        kFF = gain;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setFF(double gain, int slotID) {
        kFF = gain;
        return REVLibError.kOk;
    }

    //CHECKSTYLE:OFF
    @Override
    public REVLibError setIZone(double IZone) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setIZone(double IZone, int slotID) {
        return REVLibError.kOk;
    }
    //CHECKSTYLE:ON

    @Override
    public REVLibError setOutputRange(double min, double max) {
        kMinOutput = min;
        kMaxOutput = max;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setOutputRange(double min, double max, int slotID) {
        kMinOutput = min;
        kMaxOutput = max;
        return REVLibError.kOk;
    }

    @Override
    public double getP() {
        return kP;
    }

    @Override
    public double getP(int slotID) {
        return kP;
    }

    @Override
    public double getI() {
        return kI;
    }

    @Override
    public double getI(int slotID) {
        return kI;
    }

    @Override
    public double getD() {
        return kD;
    }

    @Override
    public double getD(int slotID) {
        return kD;
    }

    @Override
    public double getDFilter(int slotID) {
        return 0;
    }

    @Override
    public double getFF() {
        return kFF;
    }

    @Override
    public double getFF(int slotID) {
        return kFF;
    }

    @Override
    public double getIZone() {
        return 0;
    }

    @Override
    public double getIZone(int slotID) {
        return 0;
    }

    @Override
    public double getOutputMin() {
        return kMinOutput;
    }

    @Override
    public double getOutputMin(int slotID) {
        return kMinOutput;
    }

    @Override
    public double getOutputMax() {
        return kMaxOutput;
    }

    @Override
    public double getOutputMax(int slotID) {
        return kMaxOutput;
    }

    @Override
    public REVLibError setSmartMotionMaxVelocity(double maxVel, int slotID) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSmartMotionMaxAccel(double maxAccel, int slotID) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSmartMotionMinOutputVelocity(double minVel, int slotID) {
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setSmartMotionAllowedClosedLoopError(double allowedErr, int slotID) {
        return REVLibError.kOk;
    }

    @Override
    public double getSmartMotionMaxVelocity(int slotID) {
        return 0;
    }

    @Override
    public double getSmartMotionMaxAccel(int slotID) {
        return 0;
    }

    @Override
    public double getSmartMotionMinOutputVelocity(int slotID) {
        return 0;
    }

    @Override
    public double getSmartMotionAllowedClosedLoopError(int slotID) {
        return 0;
    }
    
    @Override
    public REVLibError setIMaxAccum(double iMaxAccum, int slotID) {
        return REVLibError.kOk;
    }

    @Override
    public double getIMaxAccum(int slotID) {
        return 0;
    }

    @Override
    public REVLibError setIAccum(double iAccum) {
        return REVLibError.kOk;
    }

    @Override
    public double getIAccum() {
        return 0;
    }

    @Override
    public REVLibError restoreFactoryDefaults() {
        return REVLibError.kOk;
    }

    @Override
    public CANSparkMax getInternalSparkMax() {
        return null;
    }

    @Override
    public REVLibError setReference(double value, ControlType ctrl) {
        referenceValue = value - positionOffset;
        controlType = ctrl;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setReference(double value, ControlType ctrl, int pidSlot) {
        referenceValue = value - positionOffset;
        controlType = ctrl;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward) {
        referenceValue = value - positionOffset;
        controlType = ctrl;
        return REVLibError.kOk;
    }

    @Override
    public REVLibError setReference(double value, ControlType ctrl, int pidSlot, double arbFeedforward,
            ArbFFUnits arbFFUnits) {
        referenceValue = value - positionOffset;
        controlType = ctrl;
        return REVLibError.kOk;
    }

    public double getReference() {
        return referenceValue + positionOffset;
    }

    public ControlType getControlType() {
        return controlType;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    private void clearPid() {
        referenceValue = 0;
        controlType = null;
    }

    @Override
    public JSONObject getSimulationData() {
        JSONObject motorObject;

        if (controlType == ControlType.kPosition) {
            motorObject = buildMotorObject(policeTicket, (float)(referenceValue / simulationScalingValue * inversionFactor()));
            motorObject.put("mode", "POSITION");
        } else {
            motorObject = buildMotorObject(policeTicket, (float)(get() * inversionFactor()));
        }

        return motorObject;
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        BigDecimal intermediate = (BigDecimal) payload.get("EncoderTicks");
        simulationPosition = (intermediate.doubleValue() * simulationScalingValue * inversionFactor());
    }

    @Override
    public void setForwardLimitSwitch(Type switchType, boolean enabled) {        
    }

    @Override
    public void setReverseLimitSwitch(Type switchType, boolean enabled) {
    }

    @Override
    public boolean getForwardLimitSwitchPressed(Type switchType) {
        return false;
    }

    @Override
    public boolean getReverseLimitSwitchPressed(Type switchType) {
        return false;
    }
}