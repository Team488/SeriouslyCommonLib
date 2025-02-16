package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix6.configs.Pigeon2Configurator;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.sim.Pigeon2SimState;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.LinearAcceleration;
import edu.wpi.first.units.measure.Temperature;
import edu.wpi.first.units.measure.Time;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.util.sendable.SendableBuilder;
import xbot.common.controls.sensors.XCANImu;
import xbot.common.injection.electrical_contract.CANBusId;

import java.util.function.BooleanSupplier;

public class Pigeon2Adapter extends XCANImu {

    private final Pigeon2 pigeon;

    public Pigeon2Adapter(CANBusId busId, int deviceId) {
        this.pigeon = new Pigeon2(deviceId, busId.id());
    }

    public void close() {
        pigeon.close();
    }

    @Override
    public Rotation2d getRotation2d() {
        return pigeon.getRotation2d();
    }

    @Override
    public LinearAcceleration getAccelerationX() {
        return pigeon.getAccelerationX().getValue();
    }

    public Boolean getStickyFault_UnlicensedFeatureInUse() {
        return pigeon.getStickyFault_UnlicensedFeatureInUse().getValue();
    }

    public AngularVelocity getAngularVelocityZWorld() {
        return pigeon.getAngularVelocityZWorld().getValue();
    }

    @Override
    public LinearAcceleration getAccelerationY() {
        return pigeon.getAccelerationY().getValue();
    }

    @Override
    public Angle getYaw() {
        return pigeon.getYaw().getValue();
    }

    @Override
    public Angle getPitch() {
        return pigeon.getPitch().getValue();
    }

    @Override
    public boolean isConnected() {
        return pigeon.isConnected();
    }

    @Override
    public LinearAcceleration getAccelerationZ() {
        return pigeon.getAccelerationZ().getValue();
    }

    @Override
    public Angle getRoll() {
        return pigeon.getRoll().getValue();
    }

    public Pigeon2Configurator getConfigurator() {
        return pigeon.getConfigurator();
    }

    public Double getQuatW() {
        return pigeon.getQuatW().getValue();
    }

    public AngularVelocity getAngularVelocityXDevice() {
        return pigeon.getAngularVelocityXDevice().getValue();
    }

    @Override
    public Voltage getSupplyVoltage() {
        return pigeon.getSupplyVoltage().getValue();
    }

    public AngularVelocity getAngularVelocityYDevice() {
        return pigeon.getAngularVelocityYDevice().getValue();
    }

    public Double getQuatX() {
        return pigeon.getQuatX().getValue();
    }

    @Override
    public Rotation3d getRotation3d() {
        return pigeon.getRotation3d();
    }

    public Double getQuatY() {
        return pigeon.getQuatY().getValue();
    }

    @Override
    public void setYaw(Angle newValue) {
        pigeon.setYaw(newValue);
    }

    public Double getQuatZ() {
        return pigeon.getQuatZ().getValue();
    }

    public AngularVelocity getAngularVelocityZDevice() {
        return pigeon.getAngularVelocityZDevice().getValue();
    }

    public Double getGravityVectorX() {
        return pigeon.getGravityVectorX().getValue();
    }

    public Double getMagneticFieldY() {
        return pigeon.getMagneticFieldY().getValue();
    }

    public Double getMagneticFieldX() {
        return pigeon.getMagneticFieldX().getValue();
    }

    public Double getMagneticFieldZ() {
        return pigeon.getMagneticFieldZ().getValue();
    }

    public Double getGravityVectorY() {
        return pigeon.getGravityVectorY().getValue();
    }

    public Double getGravityVectorZ() {
        return pigeon.getGravityVectorZ().getValue();
    }

    @Override
    public Temperature getTemperature() {
        return pigeon.getTemperature().getValue();
    }

    public Double getRawMagneticFieldX() {
        return pigeon.getRawMagneticFieldX().getValue();
    }

    public Boolean getNoMotionEnabled() {
        return pigeon.getNoMotionEnabled().getValue();
    }

    public Double getRawMagneticFieldZ() {
        return pigeon.getRawMagneticFieldZ().getValue();
    }

    public Double getRawMagneticFieldY() {
        return pigeon.getRawMagneticFieldY().getValue();
    }

    public Double getNoMotionCount() {
        return pigeon.getNoMotionCount().getValue();
    }

    public Boolean getTemperatureCompensationDisabled() {
        return pigeon.getTemperatureCompensationDisabled().getValue();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        pigeon.initSendable(builder);
    }

    public Time getUpTime() {
        return pigeon.getUpTime().getValue();
    }

    @Override
    public Angle getAccumGyroX() {
        return pigeon.getAccumGyroX().getValue();
    }

    public String getNetwork() {
        return pigeon.getNetwork();
    }

    @Override
    public void reset() {
        pigeon.reset();
    }

    @Override
    public Angle getAccumGyroY() {
        return pigeon.getAccumGyroY().getValue();
    }

    @Override
    public Angle getAccumGyroZ() {
        return pigeon.getAccumGyroZ().getValue();
    }

    public AngularVelocity getAngularVelocityXWorld() {
        return pigeon.getAngularVelocityXWorld().getValue();
    }

    public Pigeon2SimState getSimState() {
        return pigeon.getSimState();
    }

    public AngularVelocity getAngularVelocityYWorld() {
        return pigeon.getAngularVelocityYWorld().getValue();
    }

    public BooleanSupplier getResetOccurredChecker() {
        return pigeon.getResetOccurredChecker();
    }
}
