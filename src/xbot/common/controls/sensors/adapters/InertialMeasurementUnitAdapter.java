package xbot.common.controls.sensors.adapters;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.NavImu;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.navx.AHRS;
import xbot.common.math.ContiguousHeading;

public class InertialMeasurementUnitAdapter extends NavImu implements XGyro {

    AHRS ahrs;
    boolean isBroken = false;

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

    public InertialMeasurementUnitAdapter(SPI.Port spi_port_id) {
        super(ImuType.navX);
        this.ahrs = new AHRS(spi_port_id);
        this.ahrs.zeroYaw();
    }

    public InertialMeasurementUnitAdapter(I2C.Port i2c_port_id) {
        super(ImuType.navX);
        this.ahrs = new AHRS(i2c_port_id);
        this.ahrs.zeroYaw();
    }

    public InertialMeasurementUnitAdapter(SerialPort.Port serial_port_id) {
        super(ImuType.navX);
        this.ahrs = new AHRS(serial_port_id);
        this.ahrs.zeroYaw();
    }

    @Override
    public boolean isConnected() {
        return this.ahrs.isConnected();
    }

    @Override
    public ContiguousHeading getYaw() {
        return new ContiguousHeading(this.ahrs.getYaw());
    }

    @Override
    public double getRoll() {
        return this.ahrs.getRoll();
    }

    @Override
    public double getPitch() {
        return this.ahrs.getPitch();
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }


}
