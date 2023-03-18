package xbot.common.controls.sensors.mock_adapters;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.CANCoderFaults;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.CANCoderStickyFaults;

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
import xbot.common.math.WrappedRotation2d;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;
import xbot.common.simulation.ISimulatableSensor;

public class MockCANCoder extends XCANCoder implements ISimulatableSensor {

    private final int deviceId;
    private final BooleanProperty inverted;
    private final DoubleProperty simulationScale;
    private final DoubleProperty positionOffset;

    private double velocity;
    private WrappedRotation2d absolutePosition;

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
        this.absolutePosition = new WrappedRotation2d(0);
        pf.setDefaultLevel(Property.PropertyLevel.Debug);
        this.positionOffset = pf.createEphemeralProperty("PositionOffset", 0);
        this.inverted = pf.createEphemeralProperty("Inverted", deviceInfo.inverted);
        this.simulationScale = pf.createEphemeralProperty("SimulationScale", deviceInfo.simulationScalingValue);

        police.registerDevice(DeviceType.CAN, deviceInfo.channel, this);
    }
    
    @Override
    public int getDeviceId() {
        return this.deviceId;
    }

    public double getPosition_internal() {
        return WrappedRotation2d.fromDegrees(this.absolutePosition.getDegrees() + this.positionOffset.get()).getDegrees() * (inverted.get() ? -1 : 1);
    }

    public double getAbsolutePosition_internal() {
        return this.absolutePosition.getDegrees() * (inverted.get() ? -1 : 1);
    }

    public double getVelocity_internal() {
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
            payload.getBigDecimal("EncoderTicks").doubleValue() * this.simulationScale.get()
        );
    }

    public DeviceHealth getHealth_internal() {
        return DeviceHealth.Healthy;
    }



    @Override
    public ErrorCode setStatusFramePeriod(CANCoderStatusFrame frame, int periodMs) {
        return ErrorCode.OK;
    }

    @Override
    public int getStatusFramePeriod(CANCoderStatusFrame frame) {
        return 20;
    }

    @Override
    public ErrorCode getFaults(CANCoderFaults toFill) {
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode getStickyFaults(CANCoderStickyFaults toFill) {
        return ErrorCode.OK;
    }

    @Override
    public ErrorCode clearStickyFaults() {
        return ErrorCode.OK;
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
