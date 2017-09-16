package xbot.common.controls.sensors.adapters;

import com.google.inject.Inject;
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

    @Inject
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

    @Inject
    public InertialMeasurementUnitAdapter(@Assisted("spi-port") SPI.Port spi_port_id) {
        super(ImuType.navX);
        try {
            this.ahrs = new AHRS(spi_port_id);
        }
        catch (Exception e){
            isBroken = true;
        }
    }

    @Inject
    public InertialMeasurementUnitAdapter(@Assisted("i2c-port") I2C.Port i2c_port_id) {
        super(ImuType.navX);
        try {
            this.ahrs = new AHRS(i2c_port_id);
        }
        catch (Exception e){
            isBroken = true;
        }
    }

    @Inject
    public InertialMeasurementUnitAdapter(@Assisted("serial-port") SerialPort.Port serial_port_id) {
        super(ImuType.navX);
        try {
            this.ahrs = new AHRS(serial_port_id);
        }
        catch (Exception e){
            isBroken = true;
        }
    }

    @Override
    public boolean isConnected() {
        if (!isBroken) {
            return this.ahrs.isConnected();
        }
        return false;
    }

    protected double getYaw() {
        if (!isBroken) {
            return -this.ahrs.getYaw();
        }
        return 0;
    }

    @Override
    public double getRoll() {
        if (!isBroken) {
            return -this.ahrs.getRoll();
        }
        return 0;
    }

    @Override
    public double getPitch() {
        if (!isBroken) {
            return -this.ahrs.getPitch();
        }
        return 0;
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }
    
    /**
     * Note: this is in degrees per second.
     */
    public double getYawAngularVelocity(){
        if (!isBroken) {
            return ahrs.getRate();
        }
        return 0;
    }
}
