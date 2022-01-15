package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.kauailabs.navx.frc.AHRS;

import org.apache.log4j.Logger;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public class InertialMeasurementUnitAdapter extends XGyro {

    AHRS ahrs;
    boolean isBroken = false;

    static Logger log = Logger.getLogger(InertialMeasurementUnitAdapter.class);
    
    @AssistedInject
    public InertialMeasurementUnitAdapter() {
        super(ImuType.navX);
        /* Options: Port.kMXP, SPI.kMXP, I2C.kMXP or SerialPort.kUSB */
        try {
            this.ahrs = new AHRS(SPI.Port.kMXP);
            log.info("AHRS successfully created");
        }
        catch (Exception e){
            isBroken = true;
            log.warn("AHRS could not be created - gyro is broken!");
        }
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(ImuType.navX);
        police.registerDevice(DeviceType.IMU, channel, this);
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(@Assisted("spi-port") SPI.Port spi_port_id) {
        super(ImuType.navX);
        this.ahrs = new AHRS(spi_port_id);
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(@Assisted("i2c-port") I2C.Port i2c_port_id) {
        super(ImuType.navX);
        this.ahrs = new AHRS(i2c_port_id);
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(@Assisted("serial-port") SerialPort.Port serial_port_id) {
        super(ImuType.navX);
        this.ahrs = new AHRS(serial_port_id);
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
