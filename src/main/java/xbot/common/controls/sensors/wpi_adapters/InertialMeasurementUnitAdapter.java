package xbot.common.controls.sensors.wpi_adapters;

import com.kauailabs.navx.frc.AHRS;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public class InertialMeasurementUnitAdapter extends XGyro {

    AHRS ahrs;
    boolean isBroken = false;

    static Logger log = LogManager.getLogger(InertialMeasurementUnitAdapter.class);
    
    @AssistedFactory
    public abstract static class InertialMeasurementUnitAdapterFactory extends XGyroFactory {
        public abstract InertialMeasurementUnitAdapter create(@Assisted SPI.Port spiPort, @Assisted SerialPort.Port serialPort, @Assisted I2C.Port i2cPort);
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(DevicePolice police, @Assisted SPI.Port spiPort, @Assisted SerialPort.Port serialPort, @Assisted I2C.Port i2cPort) {
        super(ImuType.navX);
        /* Options: Port.kMXP, SPI.kMXP, I2C.kMXP or SerialPort.kUSB */
        try {
            if (spiPort != null) {
                this.ahrs = new AHRS(spiPort);
                police.registerDevice(DeviceType.SPI, spiPort.value, this);
            } else if (serialPort != null) {
                this.ahrs = new AHRS(serialPort);
                police.registerDevice(DeviceType.IMU, serialPort.value, this);
            } else if (i2cPort != null) {
                this.ahrs = new AHRS(i2cPort);
                police.registerDevice(DeviceType.I2C, i2cPort.value, this);
            } else {
                throw new Exception("No port provided");
            }
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
}
