package xbot.common.controls.sensors;

import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;
import org.wpilib.math.linalg.VecBuilder;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.LinearAcceleration;
import org.littletonrobotics.junction.Logger;

import xbot.common.advantage.DataFrameRefreshable;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.controls.io_inputs.XGyroIoInputsAutoLogged;
import xbot.common.injection.electrical_contract.IMUInfo;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;
import static org.wpilib.units.Units.MetersPerSecondPerSecond;

public abstract class XGyro implements DataFrameRefreshable, AutoCloseable
{
    public enum InterfaceType {
        CAN,
        spi,
        serial,
        i2c
    }

    public enum ImuType {
        onboard,
        mock,
        pigeon2
    }

    protected final ImuType imuType;
    protected final String deviceName;

    protected final XGyroIoInputsAutoLogged io;

    public abstract static class XGyroFactory {
        public abstract XGyro create(IMUInfo imuInfo);

        public XGyro create() {
            return create(new IMUInfo(MountOrientation.FLAT));
        }
    }

    protected XGyro(IMUInfo info, DataFrameRegistry dataFrameRegistry)
    {
        this.imuType = info.imuType();
        this.deviceName = info.name();
        this.io = new XGyroIoInputsAutoLogged();
        dataFrameRegistry.register(this);
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

    public LinearAcceleration getAccelerationX() {
        return io.acceleration[0];
    }

    public LinearAcceleration getAccelerationY() {
        return io.acceleration[1];
    }

    public LinearAcceleration getAccelerationZ() {
        return io.acceleration[2];
    }

    public LinearAcceleration getAcceleration() {
        return MetersPerSecondPerSecond.of(VecBuilder.fill(
            getAccelerationX().in(MetersPerSecondPerSecond),
            getAccelerationY().in(MetersPerSecondPerSecond),
            getAccelerationX().in(MetersPerSecondPerSecond)
        ).norm());
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

    protected abstract void updateInputs(XGyroIoInputs inputs);

    public void refreshDataFrame() {
        updateInputs(io);
        Logger.processInputs(this.deviceName, io);
    }
}
