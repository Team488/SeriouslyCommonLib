package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix6.hardware.Pigeon2;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.CANBusId;
import xbot.common.injection.electrical_contract.IMUInfo;

import static edu.wpi.first.units.Units.Degrees;
import static edu.wpi.first.units.Units.DegreesPerSecond;

public class Pigeon2Adapter extends XGyro {

    private final Pigeon2 pigeon;
    static Logger log = LogManager.getLogger(Pigeon2Adapter.class);
    boolean isBroken;

    @AssistedFactory
    public abstract static class Pigeon2AdapterFactory extends XGyroFactory {
        public abstract Pigeon2Adapter create(@Assisted IMUInfo imuInfo);
    }

    @AssistedInject
    public Pigeon2Adapter(DevicePolice police, @Assisted IMUInfo imuInfo) {
        super(ImuType.pigeon2);
        this.pigeon = new Pigeon2(imuInfo.deviceId(), imuInfo.canBusId().id());
    }

    public void close() {
        pigeon.close();
    }

    @Override
    public boolean isBroken() {
        return !pigeon.isConnected();
    }

    @Override
    protected void updateInputs(XGyroIoInputs inputs) {
        inputs.yaw = pigeon.getYaw().getValue().in(Degrees);
        inputs.pitch = pigeon.getPitch().getValue().in(Degrees);
        inputs.roll = pigeon.getRoll().getValue().in(Degrees);
        inputs.yawAngularVelocity = pigeon.getAngularVelocityZDevice().getValue().in(DegreesPerSecond);
        inputs.isConnected = pigeon.isConnected();
    }
}
