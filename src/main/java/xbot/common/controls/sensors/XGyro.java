package xbot.common.controls.sensors;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import org.littletonrobotics.junction.Logger;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.controls.io_inputs.XGyroIoInputsAutoLogged;
import xbot.common.injection.electrical_contract.IMUInfo;
import xbot.common.math.WrappedRotation2d;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

public abstract class XGyro implements DataFrameRefreshable, AutoCloseable
{
    public enum InterfaceType {
        CAN,
        spi,
        serial,
        i2c
    }

    public enum ImuType {
        nav6,
        navX,
        mock,
        pigeon2
    }

    protected final ImuType imuType;
    protected final String deviceName;

    protected final XGyroIoInputsAutoLogged io;

    public abstract static class XGyroFactory {
        public abstract XGyro create(IMUInfo imuInfo);

        public XGyro create() {
            return create(new IMUInfo(InterfaceType.spi));
        }
    }

    protected XGyro(IMUInfo info)
    {
        this.imuType = info.imuType();
        this.deviceName = info.name();
        this.io = new XGyroIoInputsAutoLogged();
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
    public Angle getHeading() {
        if (!isBroken()) {
            return getDeviceYaw();
        }
        return Degrees.zero();
    }

    public Angle getRoll() {
        if (!isBroken()) {
            return getDeviceRoll();
        }
        return Degrees.zero();
    }

    public Angle getPitch() {
        if (!isBroken()) {
            return getDevicePitch();
        }
        return Degrees.zero();
    }

    public AngularVelocity getYawAngularVelocity() {
        if (!isBroken()) {
            return getDeviceYawAngularVelocity();
        }
        return DegreesPerSecond.zero();
    }

    public double getAccelerationX() {
        return io.acceleration[0];
    }

    public double getAccelerationY() {
        return io.acceleration[1];
    }

    public double getAccelerationZ() {
        return io.acceleration[2];
    }

    public double getAcceleration() {
        return VecBuilder.fill(getAccelerationX(), getAccelerationY(), getAccelerationX()).norm();
    }

    // What follows are the primitive "gets" for the gyro. These aren't protected,
    // and could cause exceptions if called while they gyro is not connected.

    public boolean isConnected() {
        return io.isConnected;
    }

    private Angle getDeviceRoll() {
        return io.roll;
    }

    private Angle getDevicePitch() {
        return io.pitch;
    }

    private Angle getDeviceYaw() {
        return io.yaw;
    }

    private AngularVelocity getDeviceYawAngularVelocity() {
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
        Logger.processInputs(this.deviceName, io);
    }
}
