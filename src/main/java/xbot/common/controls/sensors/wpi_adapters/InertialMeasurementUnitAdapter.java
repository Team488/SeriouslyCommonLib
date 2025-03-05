package xbot.common.controls.sensors.wpi_adapters;

import com.studica.frc.AHRS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.IMUInfo;

public class InertialMeasurementUnitAdapter extends XGyro {

    AHRS ahrs;
    boolean isBroken = false;

    static Logger log = LogManager.getLogger(InertialMeasurementUnitAdapter.class);

    @AssistedFactory
    public abstract static class InertialMeasurementUnitAdapterFactory extends XGyroFactory {
        public abstract InertialMeasurementUnitAdapter create(@Assisted IMUInfo imuInfo);
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(DevicePolice police, @Assisted IMUInfo imuInfo) {
        super(ImuType.navX);
        /* Options: Port.kMXP, SPI.kMXP, I2C.kMXP or SerialPort.kUSB */
        try {
            switch (imuInfo.interfaceType()) {
                case spi -> this.ahrs = new AHRS(AHRS.NavXComType.kMXP_SPI);
                case serial -> this.ahrs = new AHRS(AHRS.NavXComType.kMXP_UART);
                case i2c -> this.ahrs = new AHRS(AHRS.NavXComType.kI2C);
                default -> this.ahrs = new AHRS(AHRS.NavXComType.kMXP_SPI);
            }
            police.registerDevice(DeviceType.IMU, 1, this);
            log.info("AHRS successfully created");
        }
        catch (Exception e){
            isBroken = true;
            log.warn("AHRS could not be created - gyro is broken!");
        }
    }

    public boolean isConnected() {
        return this.ahrs.isConnected();
    }

    protected double getDeviceYaw() {
        return -this.ahrs.getYaw();
    }

    public double getDeviceRoll() {
        return -this.ahrs.getRoll();
    }

    public double getDevicePitch() {
        return -this.ahrs.getPitch();
    }

    @Override
    protected void updateInputs(XGyroIoInputs inputs) {
        inputs.yaw = getDeviceYaw();
        inputs.yawAngularVelocity = getDeviceYawAngularVelocity();
        inputs.pitch = getDevicePitch();
        inputs.roll = getDeviceRoll();
        inputs.acceleration = new double[]{
            getDeviceRawAccelX(),
            getDeviceRawAccelY(),
            getDeviceRawAccelZ()
        };
        inputs.isConnected = isConnected();
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }

    /**
     * Note: this is in degrees per second.
     */
    public double getDeviceYawAngularVelocity(){
        return ahrs.getRate();
    }

    public double getDeviceVelocityX() {
        return ahrs.getVelocityX();
    }

    public double getDeviceVelocityY() {
        return ahrs.getVelocityY();
    }

    public double getDeviceVelocityZ() {
        return ahrs.getVelocityZ();
    }

    public double getDeviceRawAccelX() {
        return ahrs.getRawAccelX();
    }

    public double getDeviceRawAccelY() {
        return ahrs.getRawAccelY();
    }

    public double getDeviceRawAccelZ() {
        return ahrs.getRawAccelZ();
    }

    @Override
    public void close() throws Exception {
        if (ahrs != null) {
            ahrs.close();
        }
    }
}
