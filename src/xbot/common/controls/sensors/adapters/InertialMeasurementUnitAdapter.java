package xbot.common.controls.sensors.adapters;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XInertialMeasurementUnit;
import xbot.common.controls.sensors.navx.AHRS;
import xbot.common.math.ContiguousDouble;

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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ContiguousDouble getYaw() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getRoll() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getPitch() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isBroken() {
        // TODO Auto-generated method stub
        return false;
    }

    
}
