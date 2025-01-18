package xbot.common.controls.sensors.mock_adapters;

import com.ctre.phoenix6.StatusCode;
import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XCANCoderInputs;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.WrappedRotation2d;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;
import xbot.common.simulation.ISimulatableSensor;

public class MockCANCoder extends XCANCoder implements ISimulatableSensor {

    private final int deviceId;
    private boolean inverted;
    private double simulationScale;
    private double positionOffset;

    private double velocity;
    private double position;

    @AssistedFactory
    public abstract static class MockCANCoderFactory implements XCANCoderFactory {
        public abstract MockCANCoder create(
            @Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public MockCANCoder(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf) {
        super(deviceInfo);
        pf.setPrefix(owningSystemPrefix);

        this.deviceId = deviceInfo.channel;
        this.velocity = 0;
        this.position = 0;
        pf.setDefaultLevel(Property.PropertyLevel.Debug);
        this.positionOffset = 0;
        this.inverted = deviceInfo.inverted;
        this.simulationScale = deviceInfo.simulationScalingValue;

        police.registerDevice(DeviceType.CAN, deviceInfo.channel, this);
    }

    @Override
    public int getDeviceId() {
        return this.deviceId;
    }

    public double getPosition_internal() {
        return position * (inverted ? -1 : 1);
    }


    public double getAbsolutePosition_internal() {
        // With the new Phoenix 6 library, the encoder now returns units of whole rotations bounded
        // between -0.5 and 0.5.
        double scaledValue = ContiguousDouble.reboundValue(position, -0.5, 0.5);
        return scaledValue * (inverted ? -1 : 1);
    }

    public double getVelocity_internal() {
        return this.velocity * (inverted ? -1 : 1);
    }

    @Override
    public void setPosition(double newPosition) {
        position = newPosition;
    }

    public double getPositionOffset() {
        return this.positionOffset;
    }

    public void setAbsolutePosition(double position) {
        this.position = position * (inverted ? -1 : 1);
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
    public StatusCode setUpdateFrequencyForPosition(double frequencyInHz) {
        return StatusCode.OK;
    }

    @Override
    public StatusCode stopAllUnsetSignals() {
        return StatusCode.OK;
    }

    @Override
    public StatusCode clearStickyFaults() {
        return StatusCode.OK;
    }

    public boolean hasResetOccurred_internal() {
        return false;
    }

    @Override
    public void updateInputs(XAbsoluteEncoderInputs inputs) {
        inputs.absolutePosition = getAbsolutePosition_internal();
        inputs.velocity = getVelocity_internal();
        inputs.position = getPosition_internal();
        inputs.deviceHealth = getHealth_internal().toString();
    }
    @Override
    public void updateInputs(XCANCoderInputs inputs) {
        inputs.hasResetOccurred = hasResetOccurred_internal();
    }
}
