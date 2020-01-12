package xbot.common.controls.actuators.mock_adapters;

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
import com.revrobotics.EncoderType;

import xbot.common.controls.actuators.XCANSparkMax;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;
import org.apache.log4j.Logger;

public class MockCANSparkMax extends XCANSparkMax {

    private static Logger log = Logger.getLogger(MockCANSparkMax.class);

    @Inject
    public MockCANSparkMax(@Assisted("deviceId") int deviceId, PropertyFactory propMan, DevicePolice police) {
        super(deviceId, propMan, police);
        log.info("Creating CAN talon with device ID: " + deviceId);

        this.deviceId = deviceId;
    }

    @Override
    public void set(double speed) {

    }

    @Override
    public void setVoltage(double outputVolts) {

    }

    @Override
    public double get() {

        return 0;
    }

    @Override
    public void setInverted(boolean isInverted) {

    }

    @Override
    public boolean getInverted() {

        return false;
    }

    @Override
    public void disable() {

    }

    @Override
    public void stopMotor() {

    }

    @Override
    public void pidWrite(double output) {

    }

    @Override
    public CANEncoder getEncoder() {

        return null;
    }

    @Override
    public CANEncoder getEncoder(EncoderType sensorType, int counts_per_rev) {

        return null;
    }

    @Override
    public CANEncoder getAlternateEncoder() {

        return null;
    }

    @Override
    public CANEncoder getAlternateEncoder(AlternateEncoderType sensorType, int counts_per_rev) {

        return null;
    }

    @Override
    public CANAnalog getAnalog(AnalogMode mode) {

        return null;
    }

    @Override
    public CANPIDController getPIDController() {

        return null;
    }

    @Override
    public CANDigitalInput getForwardLimitSwitch(LimitSwitchPolarity polarity) {

        return null;
    }

    @Override
    public CANDigitalInput getReverseLimitSwitch(LimitSwitchPolarity polarity) {

        return null;
    }

    @Override
    public CANError setSmartCurrentLimit(int limit) {

        return null;
    }

    @Override
    public CANError setSmartCurrentLimit(int stallLimit, int freeLimit) {

        return null;
    }

    @Override
    public CANError setSmartCurrentLimit(int stallLimit, int freeLimit, int limitRPM) {

        return null;
    }

    @Override
    public CANError setSecondaryCurrentLimit(double limit) {

        return null;
    }

    @Override
    public CANError setSecondaryCurrentLimit(double limit, int chopCycles) {

        return null;
    }

    @Override
    public CANError setIdleMode(IdleMode mode) {

        return null;
    }

    @Override
    public IdleMode getIdleMode() {

        return null;
    }

    @Override
    public CANError enableVoltageCompensation(double nominalVoltage) {

        return null;
    }

    @Override
    public CANError disableVoltageCompensation() {

        return null;
    }

    @Override
    public double getVoltageCompensationNominalVoltage() {

        return 0;
    }

    @Override
    public CANError setOpenLoopRampRate(double rate) {

        return null;
    }

    @Override
    public CANError setClosedLoopRampRate(double rate) {

        return null;
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
    public CANError follow(CANSparkMax leader) {

        return null;
    }

    @Override
    public CANError follow(CANSparkMax leader, boolean invert) {

        return null;
    }

    @Override
    public CANError follow(ExternalFollower leader, int deviceID) {

        return null;
    }

    @Override
    public CANError follow(ExternalFollower leader, int deviceID, boolean invert) {

        return null;
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
    public CANError clearFaults() {

        return null;
    }

    @Override
    public CANError burnFlash() {

        return null;
    }

    @Override
    public CANError setCANTimeout(int milliseconds) {

        return null;
    }

    @Override
    public CANError enableSoftLimit(SoftLimitDirection direction, boolean enable) {

        return null;
    }

    @Override
    public CANError setSoftLimit(SoftLimitDirection direction, float limit) {

        return null;
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
    public CANError getLastError() {

        return null;
    }

}