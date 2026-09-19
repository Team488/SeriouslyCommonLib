package xbot.common.controls.sensors.wpi_adapters;

import org.wpilib.hardware.imu.OnboardIMU;
import org.wpilib.units.measure.Acceleration;
import org.wpilib.units.measure.Angle;
import org.wpilib.units.measure.AngularVelocity;
import org.wpilib.units.measure.LinearAcceleration;
import org.wpilib.units.measure.Velocity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XGyro;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.IMUInfo;

import static org.wpilib.units.Units.MetersPerSecondPerSecond;
import static org.wpilib.units.Units.Radians;
import static org.wpilib.units.Units.RadiansPerSecond;

public class InertialMeasurementUnitAdapter extends XGyro {

    OnboardIMU imu;
    boolean isBroken = false;

    static Logger log = LogManager.getLogger(InertialMeasurementUnitAdapter.class);

    @AssistedFactory
    public abstract static class InertialMeasurementUnitAdapterFactory extends XGyroFactory {
        public abstract InertialMeasurementUnitAdapter create(@Assisted IMUInfo imuInfo);
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(DevicePolice police, DataFrameRegistry registry, @Assisted IMUInfo imuInfo) {
        super(imuInfo, registry);
        try {
            this.imu = new OnboardIMU(imuInfo.mountOrientation());
            police.registerDevice(DeviceType.IMU, imuInfo.deviceId(), this);
            log.info("IMU successfully created");
        }
        catch (Exception e){
            isBroken = true;
            log.warn("IMU could not be created - gyro is broken!");
        }
    }

    public boolean isConnected() {
        return true;
    }

    private Angle getDeviceYaw() {
        return Radians.of(-this.imu.getYawRadians());
    }

    private Angle getDeviceRoll() {
        return Radians.of(-this.imu.getRotation3d().getX());
    }

    private Angle getDevicePitch() {
        return Radians.of(-this.imu.getRotation3d().getY());
    }

    @Override
    protected void updateInputs(XGyroIoInputs inputs) {
        inputs.yaw = getDeviceYaw();
        inputs.yawAngularVelocity = getDeviceYawAngularVelocity();
        inputs.pitch = getDevicePitch();
        inputs.roll = getDeviceRoll();
        inputs.acceleration = new LinearAcceleration[]{
            getDeviceRawAccelX(),
            getDeviceRawAccelY(),
            getDeviceRawAccelZ()
        };
        inputs.isConnected = isConnected();
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }

    public AngularVelocity getDeviceYawAngularVelocity(){
        return RadiansPerSecond.of(imu.getGyroRateZ());
    }

    public LinearAcceleration getDeviceRawAccelX() {
        return MetersPerSecondPerSecond.of(imu.getAccelX());
    }

    public LinearAcceleration getDeviceRawAccelY() {
        return MetersPerSecondPerSecond.of(imu.getAccelY());
    }

    public LinearAcceleration getDeviceRawAccelZ() {
        return MetersPerSecondPerSecond.of(imu.getAccelZ());
    }

    @Override
    public void close() {
        return;
    }
}
