package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix6.BaseStatusSignal;
import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.StatusSignal;
import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.SensorDirectionValue;
import edu.wpi.first.units.measure.Angle;
import edu.wpi.first.units.measure.AngularVelocity;
import edu.wpi.first.units.measure.Velocity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.command.DataFrameRegistry;
import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XCANCoderInputs;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

import static edu.wpi.first.units.Units.Rotations;
import static edu.wpi.first.units.Units.RotationsPerSecond;

public class CANCoderAdapter extends XCANCoder {

    private static final Logger log = LogManager.getLogger(CANCoderAdapter.class);

    private final int deviceId;
    private final CANcoder cancoder;

    private double magnetOffset;
    private boolean inverted;

    private final StatusSignal<Integer> versionSignal;
    private final StatusSignal<Angle> positionSignal;
    private final StatusSignal<Angle> absolutePositionSignal;
    private final StatusSignal<AngularVelocity> velocitySignal;

    @AssistedFactory
    public abstract static class CANCoderAdapterFactory implements XCANCoderFactory {
        public abstract CANCoderAdapter create(
            @Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public CANCoderAdapter(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf, DataFrameRegistry dataFrameRegistry) {
        super(deviceInfo, dataFrameRegistry);
        pf.setPrefix(owningSystemPrefix);

        this.inverted = deviceInfo.inverted;
        this.magnetOffset = 0.0;

        this.cancoder = new CANcoder(deviceInfo.channel, deviceInfo.canBusId.toPhoenixCANBus());

        var currentConfig = getCurrentConfiguration();
        currentConfig.MagnetSensor.AbsoluteSensorDiscontinuityPoint = 0.5;
        currentConfig.MagnetSensor.SensorDirection = this.inverted
                ? SensorDirectionValue.Clockwise_Positive : SensorDirectionValue.CounterClockwise_Positive;
        applyConfiguration(currentConfig);

        this.getMagnetOffset();

        this.deviceId = deviceInfo.channel;

        this.versionSignal = cancoder.getVersionMajor(false);
        this.positionSignal = cancoder.getPosition(false);
        this.absolutePositionSignal = cancoder.getAbsolutePosition(false);
        this.velocitySignal = cancoder.getVelocity(false);

        police.registerDevice(DeviceType.CAN, deviceInfo.canBusId, deviceInfo.channel, this);
    }

    @Override
    public int getDeviceId() {
        return this.deviceId;
    }

    public Angle getPosition_internal() {
        return positionSignal.getValue();
    }

    public Angle getAbsolutePosition_internal() {
        return absolutePositionSignal.getValue();
    }

    public AngularVelocity getVelocity_internal() {
        return velocitySignal.getValue();
    }

    public DeviceHealth getHealth_internal() {
        if (versionSignal.getStatus().isError()) {
            return DeviceHealth.Unhealthy;
        }
        return DeviceHealth.Healthy;
    }

    @Override
    public void setPosition(Angle newPosition) {
        this.cancoder.setPosition(newPosition);
    }

    /**
     * Gets the magnet offset configured on the encoder device. Blocking call.
     * @return The magnet offset in degrees
     */
    public double getMagnetOffset() {

        // With Phoenix6, getting configuration is now a multi-step process.
        var currentConfigs = new CANcoderConfiguration();
        // Note that the "refresh" is a blocking call.
        cancoder.getConfigurator().refresh(currentConfigs);

        this.magnetOffset = currentConfigs.MagnetSensor.MagnetOffset;
        return this.magnetOffset;
    }

    /**
     * Sets the magnet offset configured on the encoder device.
     * @param offsetInDegrees The magnet offset in degrees
     * @return True on success.
     */
    public boolean setMagnetOffset(double offsetInDegrees) {

        var currentConfigs = getCurrentConfiguration();
        currentConfigs.MagnetSensor.MagnetOffset = offsetInDegrees;
        var status = applyConfiguration(currentConfigs);

        if (status.isError()) {
            log.error("Failed to set magnet offset for device " + this.getDeviceId() + ". Error code: " + status.value);
            return false;
        } else {
            this.magnetOffset = offsetInDegrees;
            return true;
        }
    }

    public StatusCode setUpdateFrequencyForPosition(double frequencyInHz) {
        return this.cancoder.getPosition().setUpdateFrequency(frequencyInHz);
    }

    public StatusCode stopAllUnsetSignals() {
        return this.cancoder.optimizeBusUtilization();
    }

    @Override
    public StatusCode clearStickyFaults() {
        return this.cancoder.clearStickyFaults();
    }

    public boolean hasResetOccurred_internal() {
        return this.cancoder.hasResetOccurred();
    }

    private CANcoderConfiguration getCurrentConfiguration() {
        var currentConfig = new CANcoderConfiguration();
        this.cancoder.getConfigurator().refresh(currentConfig);
        return currentConfig;
    }

    private StatusCode applyConfiguration(CANcoderConfiguration toApply) {
        return this.cancoder.getConfigurator().apply(toApply);
    }

    private void refreshAllSignals() {
        BaseStatusSignal.refreshAll(
            this.versionSignal,
            this.positionSignal,
            this.absolutePositionSignal,
            this.velocitySignal
        );
    }

    @Override
    public void updateInputs(XAbsoluteEncoderInputs inputs) {
        refreshAllSignals();
        inputs.deviceHealth = this.getHealth_internal().toString();
        inputs.position = this.getPosition_internal();
        inputs.absolutePosition = this.getAbsolutePosition_internal();
        inputs.velocity = this.getVelocity_internal();
    }

    @Override
    public void updateInputs(XCANCoderInputs inputs) {
        inputs.hasResetOccurred = this.hasResetOccurred_internal();
    }
}
