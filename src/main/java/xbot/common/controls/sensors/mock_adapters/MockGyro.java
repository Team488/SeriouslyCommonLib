package xbot.common.controls.sensors.mock_adapters;

import java.math.BigDecimal;

import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.LinearAcceleration;
import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XGyro;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.simulation.ISimulatableSensor;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;
import static org.wpilib.units.Units.MetersPerSecondPerSecond;

public class MockGyro extends XGyro implements ISimulatableSensor {
    private boolean isBroken;

    private Angle yaw = Degrees.zero();
    private Angle pitch = Degrees.zero();
    private Angle roll = Degrees.zero();
    private AngularVelocity yawAngularVelocity = DegreesPerSecond.zero();
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private LinearAcceleration rawAccelX = MetersPerSecondPerSecond.zero();
    private LinearAcceleration rawAccelY = MetersPerSecondPerSecond.zero();
    private LinearAcceleration rawAccelZ = MetersPerSecondPerSecond.zero();

    @AssistedFactory
    public abstract static class MockGyroFactory extends XGyroFactory {
        public abstract MockGyro create(@Assisted IMUInfo imuInfo);
    }

    @AssistedInject
    public MockGyro(DevicePolice police, DataFrameRegistry dataFrameRegistry, @Assisted IMUInfo imuInfo) {
        super(IMUInfo.createMock(imuInfo), dataFrameRegistry);
        police.registerDevice(DeviceType.IMU, imuInfo.deviceId(), this);
    }

    public boolean isConnected() {
        return true;
    }

    public void setYaw(Angle yaw) {
        this.yaw = yaw;
    }

    public Angle getDeviceYaw() {
        return yaw;
    }

    public void setIsBroken(boolean broken) {
        this.isBroken = broken;
    }

    @Override
    protected void updateInputs(XGyroIoInputs inputs) {
        inputs.yaw = yaw;
        inputs.pitch = pitch;
        inputs.roll = roll;
        inputs.yawAngularVelocity = yawAngularVelocity;
        inputs.acceleration = new LinearAcceleration[] { rawAccelX, rawAccelY, rawAccelZ };
        inputs.isConnected = true;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setRoll(Angle roll) {
        this.roll = roll;
    }

    public Angle getDeviceRoll() {
        return roll;
    }

    public void setPitch(Angle pitch) {
        this.pitch = pitch;
    }

    public Angle getDevicePitch() {
        return pitch;
    }

    public void setYawAngularVelocity(AngularVelocity yawAngularVelocity) {
        this.yawAngularVelocity = yawAngularVelocity;
    }

    public AngularVelocity getDeviceYawAngularVelocity() {
        return yawAngularVelocity;
    }

    public double getDeviceVelocityX() {
        return this.velocityX;
    }

    public void setDeviceVelocityX(double velocity) {
        this.velocityX = velocity;
    }

    public double getDeviceVelocityY() {
        return this.velocityY;
    }

    public void setDeviceVelocityY(double velocity) {
        this.velocityY = velocity;
    }

    public double getDeviceVelocityZ() {
        return this.velocityZ;
    }

    public void setDeviceVelocityZ(double velocity) {
        this.velocityZ = velocity;
    }

    public LinearAcceleration getDeviceRawAccelX() {
        return this.rawAccelX;
    }

    public void setDeviceRawAccelX(double accel) {
        setDeviceRawAccelX(MetersPerSecondPerSecond.of(accel));
    }

    public void setDeviceRawAccelX(LinearAcceleration accel) {
        this.rawAccelX = accel;
    }

    public LinearAcceleration getDeviceRawAccelY() {
        return this.rawAccelY;
    }

    public void setDeviceRawAccelY(double accel) {
        setDeviceRawAccelY(MetersPerSecondPerSecond.of(accel));
    }

    public void setDeviceRawAccelY(LinearAcceleration accel) {
        this.rawAccelY = accel;
    }

    public LinearAcceleration getDeviceRawAccelZ() {
        return this.rawAccelZ;
    }

    public void setDeviceRawAccelZ(double accel) {
        setDeviceRawAccelZ(MetersPerSecondPerSecond.of(accel));
    }

    public void setDeviceRawAccelZ(LinearAcceleration accel) {
        this.rawAccelZ = accel;
    }

    @Override
    public void close() throws Exception {
        // No-op
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        BigDecimal intermediateYaw = (BigDecimal)payload.get("Roll");
        BigDecimal intermediateYawVelocity = (BigDecimal)payload.get("YawVelocity");

        // The simulation returns values between -pi and pi, which is just like the NavX returning -180 to 180. We just need
        // to do a quick conversion.
        double yawInDegrees = intermediateYaw.doubleValue() * 180.0 / Math.PI;
        double yawVelocityInDegrees = intermediateYawVelocity.doubleValue() * 180.0 / Math.PI;

        this.setYaw(Degrees.of(yawInDegrees));
        this.setYawAngularVelocity(DegreesPerSecond.of(yawVelocityInDegrees));

        // Eventually we will have more of these for more IMU elements
    }

}
