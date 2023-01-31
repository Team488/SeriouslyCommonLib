package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.CANCoderFaults;
import com.ctre.phoenix.sensors.CANCoderStatusFrame;
import com.ctre.phoenix.sensors.CANCoderStickyFaults;
import com.ctre.phoenix.sensors.WPI_CANCoder;

import org.apache.log4j.Logger;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.io_inputs.XAbsoluteEncoderInputs;
import xbot.common.controls.io_inputs.XCANCoderInputs;
import xbot.common.controls.sensors.XCANCoder;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;
import xbot.common.resiliency.DeviceHealth;

public class CANCoderAdapter extends XCANCoder {
    
    private static final Logger log = Logger.getLogger(CANCoderAdapter.class);

    private final int deviceId;
    private final CANCoder cancoder;

    private final DoubleProperty magnetOffset;
    private final BooleanProperty inverted;

    @AssistedFactory
    public abstract static class CANCoderAdapterFactory implements XCANCoderFactory {
        public abstract CANCoderAdapter create(
            @Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix);
    }

    @AssistedInject
    public CANCoderAdapter(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf) {
        super(deviceInfo);
        pf.setPrefix(owningSystemPrefix);

        this.inverted = pf.createEphemeralProperty("Inverted", deviceInfo.inverted);
        this.magnetOffset = pf.createEphemeralProperty("Magnet offset", 0.0);
        
        this.cancoder = new WPI_CANCoder(deviceInfo.channel, "rio");
        this.cancoder.configSensorDirection(this.inverted.get());
        this.getMagnetOffset();
        
        this.deviceId = deviceInfo.channel;

        police.registerDevice(DeviceType.CAN, deviceInfo.channel, this);
    }

    @Override
    public int getDeviceId() {
        return this.deviceId;
    }

    public double getPosition_internal() {
        return this.cancoder.getPosition();
    }

    public double getAbsolutePosition_internal() {
        return this.cancoder.getAbsolutePosition();
    }

    public double getVelocity_internal() {
        return this.cancoder.getVelocity();
    }

    public DeviceHealth getHealth_internal() {
        if (this.cancoder.getFirmwareVersion() == -1) {
            return DeviceHealth.Unhealthy;
        }
        return DeviceHealth.Healthy;
    }

    @Override
    public void setPosition(double newPosition) {
        this.cancoder.setPosition(newPosition);
    }

    /**
     * Gets the magnet offset configured on the encoder device.
     * @return The magnet offset in degrees
     */
    public double getMagnetOffset() {
        this.magnetOffset.set(this.cancoder.configGetMagnetOffset());
        return this.magnetOffset.get();
    }

    /**
     * Sets the magnet offset configured on the encoder device.
     * @param offsetInDegrees The magnet offset in degrees
     * @return True on success.
     */
    public boolean setMagnetOffset(double offsetInDegrees) {
        ErrorCode errorCode = this.cancoder.configMagnetOffset(offsetInDegrees);
        if (errorCode.value != 0) {
            log.error("Failed to set magnet offset for device " + this.getDeviceId() + ". Error code: " + errorCode.value);
            return false;
        } else {
            this.magnetOffset.set(offsetInDegrees);
            return true;
        }
    }

    @Override
    public ErrorCode setStatusFramePeriod(CANCoderStatusFrame frame, int periodMs) {
        return this.cancoder.setStatusFramePeriod(frame, periodMs);
    }

    @Override
    public int getStatusFramePeriod(CANCoderStatusFrame frame) {
        return this.cancoder.getStatusFramePeriod(frame);
    }

    @Override
    public ErrorCode getFaults(CANCoderFaults toFill) {
        return this.cancoder.getFaults(toFill);
    }

    @Override
    public ErrorCode getStickyFaults(CANCoderStickyFaults toFill) {
        return this.cancoder.getStickyFaults(toFill);
    }

    @Override
    public ErrorCode clearStickyFaults() {
        return this.cancoder.clearStickyFaults();
    }

    public boolean hasResetOccurred_internal() {
        return this.cancoder.hasResetOccurred();
    }

    @Override
    public void updateInputs(XAbsoluteEncoderInputs inputs) {
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
