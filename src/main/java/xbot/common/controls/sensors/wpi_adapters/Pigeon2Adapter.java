package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.hardware.Pigeon2;
import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.LinearAcceleration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xbot.common.controls.io_inputs.XGyroIoInputs;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.electrical_contract.IMUInfo;

public class Pigeon2Adapter extends XGyro {

    private final Pigeon2 pigeon;
    static Logger log = LogManager.getLogger(Pigeon2Adapter.class);
    boolean isBroken;

    private final StatusSignal<Angle> yawSignal;
    private final StatusSignal<Angle> pitchSignal;
    private final StatusSignal<Angle> rollSignal;
    private final StatusSignal<AngularVelocity> yawAngularVelocitySignal;
    private final StatusSignal<LinearAcceleration> accelerationXSignal;
    private final StatusSignal<LinearAcceleration> accelerationYSignal;
    private final StatusSignal<LinearAcceleration> accelerationZSignal;

    @AssistedFactory
    public abstract static class Pigeon2AdapterFactory extends XGyroFactory {
        public abstract Pigeon2Adapter create(@Assisted IMUInfo imuInfo);
    }

    @AssistedInject
    public Pigeon2Adapter(DevicePolice police, @Assisted IMUInfo imuInfo) {
        super(imuInfo);
        this.pigeon = new Pigeon2(imuInfo.deviceId(), imuInfo.canBusId().toPhoenixCANBus());
        police.registerDevice(DevicePolice.DeviceType.CAN, imuInfo.canBusId(), imuInfo.deviceId(), this);

        this.yawSignal = pigeon.getYaw();
        this.pitchSignal = pigeon.getPitch();
        this.rollSignal = pigeon.getRoll();
        this.yawAngularVelocitySignal = pigeon.getAngularVelocityZDevice();
        this.accelerationXSignal = pigeon.getAccelerationX();
        this.accelerationYSignal = pigeon.getAccelerationY();
        this.accelerationZSignal = pigeon.getAccelerationZ();
    }

    public void close() {
        pigeon.close();
    }

    @Override
    public boolean isBroken() {
        return !io.isConnected;
    }

    @Override
    protected void updateInputs(XGyroIoInputs inputs) {
        BaseStatusSignal.refreshAll(
                yawSignal,
                pitchSignal,
                rollSignal,
                yawAngularVelocitySignal,
                accelerationXSignal,
                accelerationYSignal,
                accelerationZSignal
        );

        inputs.yaw = yawSignal.getValue();
        inputs.pitch = pitchSignal.getValue();
        inputs.roll = rollSignal.getValue();
        inputs.yawAngularVelocity = yawAngularVelocitySignal.getValue();
        inputs.acceleration = new double[]{
                accelerationXSignal.getValueAsDouble(),
                accelerationYSignal.getValueAsDouble(),
                accelerationZSignal.getValueAsDouble()
        };
        inputs.isConnected = pigeon.isConnected();
    }
}
