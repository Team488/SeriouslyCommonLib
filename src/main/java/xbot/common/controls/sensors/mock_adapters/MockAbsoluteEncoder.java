package xbot.common.controls.sensors.mock_adapters;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import org.json.JSONObject;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.math.WrappedRotation2d;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;
import xbot.common.simulation.ISimulatableSensor;

import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Rotations;

public class MockAbsoluteEncoder extends XAbsoluteEncoder implements ISimulatableSensor {

    private final int deviceId;
    private boolean inverted;
    private double simulationScale;
    private double positionOffset;

    private AngularVelocity velocity;
    private Angle position;

    @AssistedFactory
    public abstract static class MockAbsoluteEncoderFactory implements XAbsoluteEncoderFactory {
        public abstract MockAbsoluteEncoder create(
            @Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public MockAbsoluteEncoder(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf, DataFrameRegistry dataFrameRegistry) {
        super(deviceInfo, dataFrameRegistry);
        pf.setPrefix(owningSystemPrefix);

        this.deviceId = deviceInfo.channel;
        this.velocity = RPM.zero();
        this.position = Rotations.zero();
        police.registerDevice(DeviceType.CAN, deviceInfo.channel, this);
    }

    @Override
    public int getDeviceId() {
        return this.deviceId;
    }

    public Angle getPosition_internal() {
        return this.position.plus(Rotations.of(this.positionOffset)).times(inverted ? -1 : 1);
    }

    public void setPosition_internal(Angle position) {
        this.position = position;
    }

    public Angle getAbsolutePosition_internal() {
        return Rotations.of(MathUtil.inputModulus(this.getPosition_internal().in(Rotations), -0.5, 0.5));
    }

    public AngularVelocity getVelocity_internal() {
        return this.velocity.times(inverted ? -1 : 1);
    }

    @Override
    public void setPosition(Angle newPosition) {
        this.positionOffset = newPosition.minus(this.position).in(Rotations);
    }

    public double getPositionOffset() {
        return this.positionOffset;
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setPosition(
            Rotations.of(payload.getBigDecimal("EncoderTicks").doubleValue() * this.simulationScale)
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
