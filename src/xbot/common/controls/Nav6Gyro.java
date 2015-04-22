package xbot.common.controls;

import edu.wpi.first.wpilibj.SerialPort;
import xbot.common.math.ContiguousDouble;
import xbot.common.wpi_extensions.mechanism_wrappers.XGyro;
import xbot.nav6.IMU;

/**
 * Internal class to handle gyro readings.
 * 
 * Wraps yaw values to circle 0-360 degrees.
 *
 */
public class Nav6Gyro implements XGyro
{
    private SerialPort imuPort;
    private IMU coreSensor;
    private ContiguousDouble yawValue;
    
    public final int baudRate = 57600;
    
    public Nav6Gyro()
    {
        imuPort = new SerialPort(baudRate, SerialPort.Port.kOnboard);
        coreSensor = new IMU(imuPort);
        
        // This will remap original values. IMU reports -180 to 180 normally.
        yawValue = new ContiguousDouble(0, 360);
    }
    
    public boolean isConnected()
    {
        return coreSensor.isConnected();
    }
    
    public ContiguousDouble getYawContiguous()
    {
        yawValue.setValue(coreSensor.getYaw());
        return yawValue.clone();
    }
    
    public ContiguousDouble getYaw()
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
