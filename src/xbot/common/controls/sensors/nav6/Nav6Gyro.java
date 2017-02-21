package xbot.common.controls.sensors.nav6;

import edu.wpi.first.wpilibj.SerialPort;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.navx.AHRS;
import xbot.common.math.ContiguousHeading;

/**
 * Internal class to handle gyro readings.
 * 
 * Wraps yaw values to circle 0-360 degrees.
 *
 */
public class Nav6Gyro extends XGyro
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
    
    public double getYaw()
    {
        return coreSensor.getYaw();
    }

    public double getRoll() {
        return coreSensor.getRoll();
    }
    
    public double getPitch() {
        return coreSensor.getPitch();
    }

    public boolean isBroken() {
        return false;
    }
    
    public double getYawAngularVelocity(){
        // This method is not supported for the Nav6 gyro.
        return 0;
    }
}
