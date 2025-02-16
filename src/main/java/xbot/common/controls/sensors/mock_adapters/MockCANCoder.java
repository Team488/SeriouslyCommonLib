package xbot.common.controls.sensors.mock_adapters;

import com.ctre.phoenix6.StatusCode;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.MutAngle;
import edu.wpi.first.units.measure.MutAngularVelocity;
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
import xbot.common.properties.Property;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;
import xbot.common.simulation.ISimulatableSensor;

import static edu.wpi.first.units.Units.RPM;
import static edu.wpi.first.units.Units.Radians;
import static edu.wpi.first.units.Units.Rotations;

public class MockCANCoder extends XCANCoder implements ISimulatableSensor {

    private final int deviceId;
    private double simulationScale;
    private double positionOffset;

    private final boolean inverted;
    private final MutAngularVelocity velocity;
    private final MutAngle position;

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
        this.velocity = RPM.mutable(0);
        this.position = Rotations.mutable(0);
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

    public Angle getPosition_internal() {
        return position;
    }

    public Angle getAbsolutePosition_internal() {
        return Radians.of(MathUtil.angleModulus(this.position.in(Radians)));
    }

    public void setVelocity(AngularVelocity newVelocity) {
        this.velocity.mut_replace(newVelocity.times(inverted ? -1 : 1));
    }

    public AngularVelocity getVelocity_internal() {
        return this.velocity;
    }

    @Override
    public void setPosition(Angle newPosition) {
        position.mut_replace(newPosition.times(inverted ? -1 : 1));
    }

    public double getPositionOffset() {
        return this.positionOffset;
    }

    public void setAbsolutePosition(Angle position) {
        this.position.mut_replace(position.times(inverted ? -1 : 1));
    }

    @Override
    public void ingestSimulationData(JSONObject payload) {
        setAbsolutePosition(
            Rotations.of(payload.getBigDecimal("EncoderTicks").doubleValue() * this.simulationScale)
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
