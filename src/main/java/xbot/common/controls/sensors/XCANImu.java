package xbot.common.controls.sensors;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.util.sendable.SendableBuilder;

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
