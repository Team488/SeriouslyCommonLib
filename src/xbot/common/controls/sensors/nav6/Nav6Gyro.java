package xbot.common.controls.sensors.nav6;

import edu.wpi.first.wpilibj.SerialPort;
import xbot.common.controls.sensors.NavImu;
import xbot.common.controls.sensors.XGyro;
import xbot.common.math.ContiguousHeading;

/**
 * Internal class to handle gyro readings.
 * 
 * Wraps yaw values to circle 0-360 degrees.
 *
 */
public class Nav6Gyro extends NavImu implements XGyro
{
    private SerialPort imuPort;
    private IMU coreSensor;
    private ContiguousHeading yawValue;
    
    public final int baudRate = 57600;
    
    public Nav6Gyro()
    {
        super(ImuType.nav6);
        imuPort = new SerialPort(baudRate, SerialPort.Port.kOnboard);
        coreSensor = new IMU(imuPort);
        
        // This will remap original values. IMU reports -180 to 180 normally.
        yawValue = new ContiguousHeading();
    }
    
    public boolean isConnected()
    {
        return coreSensor.isConnected();
    }
    
    public ContiguousHeading getYawContiguous()
    {
        yawValue.setValue(coreSensor.getYaw());
        return yawValue.clone();
    }
    
    public ContiguousHeading getYaw()
    {
        return getYawContiguous();
    }

    @Override
    public double getRoll() {
        return coreSensor.getRoll();
    }
    
    @Override
    public double getPitch() {
        return coreSensor.getPitch();
    }

    @Override
    public boolean isBroken() {
        return false;
    }
}
