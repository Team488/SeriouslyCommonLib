package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix6.configs.Pigeon2Configurator;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.sim.Pigeon2SimState;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.util.sendable.SendableBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.controls.sensors.XCANImu;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;

import java.util.function.BooleanSupplier;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

public class Pigeon2Adapter extends XGyro {

    private final Pigeon2 pigeon;
    static Logger log = LogManager.getLogger(Pigeon2Adapter.class);

    @AssistedFactory
    public abstract static class Pigeon2AdapterFactory extends XGyroFactory {
        public abstract InertialMeasurementUnitAdapter create(@Assisted InterfaceType interfaceType);
    }

    @AssistedInject
    public Pigeon2Adapter(DevicePolice police, @Assisted InterfaceType interfaceType) {
        super(ImuType.pigeon2);
        int canId = 2;
        this.pigeon = new Pigeon2(canId, CANBusId.DefaultCanivore.id());

        police.registerDevice(DevicePolice.DeviceType.CAN, canId, this);
        log.info("Pigeon2 successfully created");
    }

    public void close() {
        pigeon.close();
    }

    @Override
    protected void updateInputs(XGyroIoInputs inputs) {
        inputs.yaw = pigeon.getYaw().getValue().in(Degrees);
        inputs.pitch = pigeon.getPitch().getValue().in(Degrees);
        inputs.roll = pigeon.getRoll().getValue().in(Degrees);
        inputs.yawAngularVelocity = pigeon.getAngularVelocityZWorld().getValue().in(DegreesPerSecond);
        inputs.isConnected = pigeon.isConnected();
    }

    @Override
    public boolean isBroken() {
        return false;
    }
}
