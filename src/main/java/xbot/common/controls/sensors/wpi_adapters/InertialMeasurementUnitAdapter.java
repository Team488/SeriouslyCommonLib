package xbot.common.controls.sensors.wpi_adapters;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.sensors.XGyro;
import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.IMUInfo;

import static org.wpilib.units.Units.Degrees;
import static org.wpilib.units.Units.DegreesPerSecond;

// TODO(2027 migration): Studica has not published a 2027-alpha-compatible Studica-java
// vendordep yet (checked https://github.com/Studica-Robotics/NavX/releases, no releases
// published at all as of this writing). Stubbed out until one is available; reports as
// permanently broken/disconnected.
public class InertialMeasurementUnitAdapter extends XGyro {

    boolean isBroken = true;

    static Logger log = LogManager.getLogger(InertialMeasurementUnitAdapter.class);

    @AssistedFactory
    public abstract static class InertialMeasurementUnitAdapterFactory extends XGyroFactory {
        public abstract InertialMeasurementUnitAdapter create(@Assisted IMUInfo imuInfo);
    }

    @AssistedInject
    public InertialMeasurementUnitAdapter(DevicePolice police, DataFrameRegistry registry, @Assisted IMUInfo imuInfo) {
        super(imuInfo, registry);
        log.warn("NavX/AHRS support is unavailable on WPILib 2027 (no vendor build yet) - gyro is broken!");
    }

    public boolean isConnected() {
        return false;
    }

    @Override
    protected void updateInputs(XGyroIoInputs inputs) {
        inputs.yaw = Degrees.zero();
        inputs.yawAngularVelocity = DegreesPerSecond.zero();
        inputs.pitch = Degrees.zero();
        inputs.roll = Degrees.zero();
        inputs.acceleration = new double[]{0, 0, 0};
        inputs.isConnected = false;
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }

    @Override
    public void close() {
    }
}
