package xbot.common.controls.sensors.adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.navx.AHRS;

public class InertialMeasurementUnitAdapter extends XGyro {

    AHRS ahrs;
    boolean isBroken = false;

    @AssistedInject
    public InertialMeasurementUnitAdapter() {
        super(ImuType.navX);
        /* Options: Port.kMXP, SPI.kMXP, I2C.kMXP or SerialPort.kUSB */
        try {
            this.ahrs = new AHRS(Port.kMXP);
        }
        catch (Exception e){
            isBroken = true;
        }
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

    protected double getYaw() {
        return -this.ahrs.getYaw();
    }

    @Override
    public double getRoll() {
        return -this.ahrs.getRoll();
    }

    @Override
    public double getPitch() {
        return -this.ahrs.getPitch();
        
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }
    
    /**
     * Note: this is in degrees per second.
     */
    public double getYawAngularVelocity(){
        return ahrs.getRate();
    }
}
