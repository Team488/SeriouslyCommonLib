package xbot.common.controls.sensors;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import org.littletonrobotics.junction.Logger;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.controls.io_inputs.XGyroIoInputsAutoLogged;
import xbot.common.math.WrappedRotation2d;

public abstract class XGyro
{
    public enum InterfaceType {
        spi,
        serial,
        i2c
    }

    public enum ImuType {
        nav6,
        navX,
        mock
    }
    
    protected ImuType imuType;

    protected XGyroIoInputsAutoLogged io;
    
    public abstract static class XGyroFactory {
        protected abstract XGyro create(InterfaceType interfaceType);

        public XGyro create() {
            return create(InterfaceType.spi);
        }
    }

    protected XGyro(ImuType imuType) 
    {
        this.imuType = imuType;
        io = new XGyroIoInputsAutoLogged();
    }
    
    public abstract boolean isBroken();
    
    protected ImuType getImuType() {
        return imuType;
    }

    // Below are the "safe" methods that return gyro information. They pay attention
    // to the state of the gyro, and as such will ideally not cause exceptions.
    
    /**
     * In degrees
     */
    public WrappedRotation2d getHeading() {
        if (!isBroken()) {
            return WrappedRotation2d.fromDegrees(getDeviceYaw());
        }
        return WrappedRotation2d.fromDegrees(0);
    }
    
    public double getRoll() {
        if (!isBroken()) {
            return getDeviceRoll();
        }
        return 0;
    }
    
    public double getPitch() {
        if (!isBroken()) {
            return getDevicePitch();
        }
        return 0;
    }
    
    public double getYawAngularVelocity() {
        if (!isBroken()) {
            return getDeviceYawAngularVelocity();
        }
        return 0;
    }
    
    // What follows are the primitive "gets" for the gyro. These aren't protected,
    // and could cause exceptions if called while they gyro is not connected.
    
    public boolean isConnected() {
        return io.isConnected;
    }
    
    /**
     * In degrees
     */
    private double getDeviceRoll() {
        return io.roll;
    }
    
    /**
     * In degrees
     */
    private double getDevicePitch() {
        return io.pitch;
    }
    
    /**
     * In degrees
     */
    private double getDeviceYaw() {
        return io.yaw;
    }
    
    /**
     * In degrees per second
     */
    private double getDeviceYawAngularVelocity() {
        return io.yawAngularVelocity;
    }


    private double getDeviceVelocityX() {
        // Not yet part of the io system
        return 0;
    }

    private double getDeviceVelocityY() {
        // Not yet part of the io system
        return 0;
    }

    private double getDeviceVelocityZ() {
        // Not yet part of the io system
        return 0;
    }

    private double getDeviceRawAccelX() {
        // Not yet part of the io system
        return 0;
    }

    private double getDeviceRawAccelY() {
        // Not yet part of the io system
        return 0;
    }

    private double getDeviceRawAccelZ() {
        // Not yet part of the io system
        return 0;
    }

    protected abstract void updateInputs(XGyroIoInputs inputs);

    public void refreshDataFrame() {
        updateInputs(io);
        // TODO: get a name for the gyro so we don't have to use a hardcoded one.
        Logger.processInputs("IMU", io);
    }
}
