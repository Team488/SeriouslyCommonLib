package xbot.common.controls.sensors.mock_adapters;

import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.math.WrappedRotation2d;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;
import xbot.common.simulation.ISimulatableSensor;

public class MockAbsoluteEncoder extends XAbsoluteEncoder implements ISimulatableSensor {

    private final int deviceId;
    private boolean inverted;
    private double simulationScale;
    private double positionOffset;

    private double velocity;
    private WrappedRotation2d absolutePosition;

    @AssistedFactory
    public abstract static class MockAbsoluteEncoderFactory implements XAbsoluteEncoderFactory {
        public abstract MockAbsoluteEncoder create(
            @Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public MockAbsoluteEncoder(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf) {
        super(deviceInfo);
        pf.setPrefix(owningSystemPrefix);

        this.deviceId = deviceInfo.channel;
        this.velocity = 0;
        this.absolutePosition = new WrappedRotation2d(0);
        police.registerDevice(DeviceType.CAN, deviceInfo.channel, this);
    }
    
    @Override
    public int getDeviceId() {
        return this.deviceId;
    }

    public double getPosition_internal() {
        return WrappedRotation2d.fromDegrees(this.absolutePosition.getDegrees() + this.positionOffset).getDegrees() * (inverted ? -1 : 1);
    }

    public double getAbsolutePosition_internal() {
        return this.absolutePosition.getDegrees() * (inverted ? -1 : 1);
    }

    public double getVelocity_internal() {
        return this.velocity * (inverted ? -1 : 1);
    }

    @Override
    public void setPosition(double newPosition) {
        this.positionOffset = (WrappedRotation2d.fromDegrees(newPosition).minus(this.absolutePosition).getDegrees());
    }
    

    public double getPositionOffset() {
        return this.positionOffset;
    }

    public void setAbsolutePosition(double position) {
        this.absolutePosition = WrappedRotation2d.fromDegrees(position * (inverted ? -1 : 1));
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setAbsolutePosition(
            payload.getBigDecimal("EncoderTicks").doubleValue() * this.simulationScale
        );
    }

    public DeviceHealth getHealth_internal() {
        return DeviceHealth.Healthy;
    }

    @Override
    public void updateInputs(XAbsoluteEncoderInputs inputs) {
        inputs.position = getPosition_internal();
        inputs.absolutePosition = getAbsolutePosition_internal();
        inputs.velocity = getVelocity_internal();
        inputs.deviceHealth = getHealth_internal().toString();
    }
}
