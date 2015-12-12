package xbot.common.controls.sensors.adapters;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XInertialMeasurementUnit;
import xbot.common.controls.sensors.navx.AHRS;

public class InertialMeasurementUnitAdapter implements XInertialMeasurementUnit {

    AHRS ahrs;

    public InertialMeasurementUnitAdapter() {
        /* Options: Port.kMXP, SPI.kMXP, I2C.kMXP or SerialPort.kUSB */
        this.ahrs = new AHRS(Port.kMXP);
        this.ahrs.zeroYaw();
    }

    public InertialMeasurementUnitAdapter(SPI.Port spi_port_id) {
        this.ahrs = new AHRS(spi_port_id);
        this.ahrs.zeroYaw();
    }

    public InertialMeasurementUnitAdapter(I2C.Port i2c_port_id) {
        this.ahrs = new AHRS(i2c_port_id);
        this.ahrs.zeroYaw();
    }

    public InertialMeasurementUnitAdapter(SerialPort.Port serial_port_id) {
        this.ahrs = new AHRS(serial_port_id);
        this.ahrs.zeroYaw();
    }

    @Override
    public boolean isConnected() {
        return this.ahrs.isConnected();
    }

    @Override
    public double getYaw() {
        return this.ahrs.getYaw();
    }

    @Override
    public double getRoll() {
        return this.ahrs.getRoll();
    }

    @Override
    public double getPitch() {
        return this.ahrs.getPitch();
    }


}
