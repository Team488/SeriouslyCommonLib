package xbot.common.controls.sensors.mock_adapters;

import java.math.BigDecimal;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
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
        @Override
        public abstract MockGyro create(@Assisted SPI.Port spiPort, @Assisted SerialPort.Port serialPort, @Assisted I2C.Port i2cPort);
    }

    @AssistedInject
    public MockGyro(DevicePolice police, @Assisted SPI.Port spiPort, @Assisted SerialPort.Port serialPort, @Assisted I2C.Port i2cPort) {
        super(ImuType.mock);
        if (spiPort != null) {
            police.registerDevice(DeviceType.IMU, spiPort.value, this);
        } else if (serialPort != null) {
            police.registerDevice(DeviceType.IMU, serialPort.value, this);
        } else if (i2cPort != null) {
            police.registerDevice(DeviceType.IMU, i2cPort.value, this);
        }
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

    @Override
    public double getDeviceVelocityX() {
        return this.velocityX;
    }

    public void setDeviceVelocityX(double velocity) {
        this.velocityX = velocity;
    }

    @Override
    public double getDeviceVelocityY() {
        return this.velocityY;
    }

    public void setDeviceVelocityY(double velocity) {
        this.velocityY = velocity;
    }

    @Override
    public double getDeviceVelocityZ() {
        return this.velocityZ;
    }

    public void setDeviceVelocityZ(double velocity) {
        this.velocityZ = velocity;
    }

    @Override
    public double getDeviceRawAccelX() {
        return this.rawAccelX;
    }

    public void setDeviceRawAccelX(double accel) {
        this.rawAccelX = accel;
    }

    @Override
    public double getDeviceRawAccelY() {
        return this.rawAccelY;
    }

    public void setDeviceRawAccelY(double accel) {
        this.rawAccelY = accel;
    }

    @Override
    public double getDeviceRawAccelZ() {
        return this.rawAccelZ;
    }

    public void setDeviceRawAccelZ(double accel) {
        this.rawAccelZ = accel;
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
