package xbot.common.controls.sensors.mock_adapters;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.json.JSONObject;

import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.math.WrappedRotation2d;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;
import xbot.common.simulation.ISimulatableSensor;

public class MockAbsoluteEncoder extends XAbsoluteEncoder implements ISimulatableSensor {

    private final int deviceId;
    private final BooleanProperty inverted;
    private final DoubleProperty simulationScale;
    private final DoubleProperty positionOffset;

    private double velocity;
    private WrappedRotation2d absolutePosition;

    @AssistedInject
    public MockAbsoluteEncoder(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf) {
        pf.setPrefix(owningSystemPrefix);

        this.deviceId = deviceInfo.channel;
        this.velocity = 0;
        this.absolutePosition = new WrappedRotation2d(0);
        this.positionOffset = pf.createEphemeralProperty("PositionOffset", 0);
        this.inverted = pf.createEphemeralProperty("Inverted", deviceInfo.inverted);
        this.simulationScale = pf.createEphemeralProperty("SimulationScale", deviceInfo.simulationScalingValue);

        police.registerDevice(DeviceType.CAN, deviceInfo.channel, this);
    }
    
    @Override
    public int getDeviceId() {
        return this.deviceId;
    }

    @Override
    public double getPosition() {
        return WrappedRotation2d.fromDegrees(this.absolutePosition.getDegrees() + this.positionOffset.get()).getDegrees() * (inverted.get() ? -1 : 1);
    }

    @Override
    public double getAbsolutePosition() {
        return this.absolutePosition.getDegrees() * (inverted.get() ? -1 : 1);
    }

    @Override
    public double getVelocity() {
        return this.velocity * (inverted.get() ? -1 : 1);
    }

    @Override
    public void setPosition(double newPosition) {
        this.positionOffset.set(WrappedRotation2d.fromDegrees(newPosition).minus(this.absolutePosition).getDegrees());
    }
    

    public double getPositionOffset() {
        return this.positionOffset.get();
    }

    public void setAbsolutePosition(double position) {
        this.absolutePosition = WrappedRotation2d.fromDegrees(position * (inverted.get() ? -1 : 1));
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setAbsolutePosition(
            payload.getBigDecimal("EncoderTicks").doubleValue() * this.simulationScale.get() + 90
        ); //temporary hack; simulation reports 0 when facing forward.";
    }

    @Override
    public DeviceHealth getHealth() {
        return DeviceHealth.Healthy;
    }
}
