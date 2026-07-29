package xbot.common.controls.sensors;

import org.wpilib.math.geometry.Rotation2d;
import org.wpilib.math.geometry.Rotation3d;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.LinearAcceleration;
import org.wpilib.units.measure.Temperature;
import org.wpilib.units.measure.Voltage;
import org.wpilib.util.sendable.SendableBuilder;

public abstract class XCANImu implements AutoCloseable {

    public abstract void reset();

    public abstract Rotation2d getRotation2d();

    public abstract Rotation3d getRotation3d();

    public abstract LinearAcceleration getAccelerationX();

    public abstract LinearAcceleration getAccelerationY();

    public abstract Angle getYaw();

    public abstract Angle getPitch();

    public abstract Angle getRoll();

    public abstract boolean isConnected();

    public abstract LinearAcceleration getAccelerationZ();

    public abstract Voltage getSupplyVoltage();

    public abstract void setYaw(Angle newValue);

    public abstract Temperature getTemperature();

    public abstract void initSendable(SendableBuilder builder);

    public abstract Angle getAccumGyroX();

    public abstract Angle getAccumGyroY();

    public abstract Angle getAccumGyroZ();
}
