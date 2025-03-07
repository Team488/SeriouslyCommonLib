package xbot.common.controls.sensors.mock_adapters;

import java.math.BigDecimal;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.simulation.ISimulatableSensor;

public class MockGyro extends XGyro implements ISimulatableSensor {
    private boolean isBroken;

    private double yaw;
    private double pitch;
    private double roll;
    private double yawAngularVelocity;
    private double velocityX;
    private double velocityY;
    private double velocityZ;
    private double rawAccelX;
    private double rawAccelY;
    private double rawAccelZ;

    @AssistedFactory
    public abstract static class MockGyroFactory extends XGyroFactory {
        public abstract MockGyro create(@Assisted IMUInfo imuInfo);
    }

    @AssistedInject
    public MockGyro(DevicePolice police, @Assisted IMUInfo imuInfo) {
        super(IMUInfo.createMock(imuInfo));
        police.registerDevice(DeviceType.IMU, imuInfo.deviceId(), this);
    }

    public boolean isConnected() {
        return true;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }

    public double getDeviceYaw() {
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
        inputs.acceleration = new double[] { rawAccelX, rawAccelY, rawAccelZ };
        inputs.isConnected = true;
    }

    public boolean isBroken() {
        return isBroken;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getDeviceRoll() {
        return roll;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getDevicePitch() {
        return pitch;
    }

    public void setYawAngularVelocity(double yawAngularVelocity) {
        this.yawAngularVelocity = yawAngularVelocity;
    }

    public double getDeviceYawAngularVelocity() {
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

    public double getDeviceRawAccelX() {
        return this.rawAccelX;
    }

    public void setDeviceRawAccelX(double accel) {
        this.rawAccelX = accel;
    }

    public double getDeviceRawAccelY() {
        return this.rawAccelY;
    }

    public void setDeviceRawAccelY(double accel) {
        this.rawAccelY = accel;
    }

    public double getDeviceRawAccelZ() {
        return this.rawAccelZ;
    }

    public void setDeviceRawAccelZ(double accel) {
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

        this.setYaw(yawInDegrees);
        this.setYawAngularVelocity(yawVelocityInDegrees);

        // Eventually we will have more of these for more IMU elements
    }

}
