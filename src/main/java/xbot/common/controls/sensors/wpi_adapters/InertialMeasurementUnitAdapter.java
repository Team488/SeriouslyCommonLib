package xbot.common.controls.sensors.wpi_adapters;

import com.kauailabs.navx.frc.AHRS;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public class InertialMeasurementUnitAdapter extends XGyro {

    AHRS ahrs;
    boolean isBroken = false;

    static Logger log = Logger.getLogger(InertialMeasurementUnitAdapter.class);
    
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

    @Override
    public boolean isConnected() {
        return this.ahrs.isConnected();
    }

    protected double getDeviceYaw() {
        return -this.ahrs.getYaw();
    }

    @Override
    public double getDeviceRoll() {
        return -this.ahrs.getRoll();
    }

    @Override
    public double getDevicePitch() {
        return -this.ahrs.getPitch();
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

    @Override
    public double getDeviceVelocityX() {
        return ahrs.getVelocityX();
    }

    @Override
    public double getDeviceVelocityY() {
        return ahrs.getVelocityY();
    }

    @Override
    public double getDeviceVelocityZ() {
        return ahrs.getVelocityZ();
    }

    @Override
    public double getDeviceRawAccelX() {
        return ahrs.getRawAccelX();
    }

    @Override
    public double getDeviceRawAccelY() {
        return ahrs.getRawAccelY();
    }

    @Override
    public double getDeviceRawAccelZ() {
        return ahrs.getRawAccelZ();
    }
}
