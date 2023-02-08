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

    @Override
    public double getPosition() {
        return this.cancoder.getPosition();
    }

    @Override
    public double getAbsolutePosition() {
        return this.cancoder.getAbsolutePosition();
    }

    @Override
    public double getVelocity() {
        return this.cancoder.getVelocity();
    }

    public DeviceHealth getHealth() {
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

    @Override
    public boolean hasResetOccurred() {
        return this.cancoder.hasResetOccurred();
    }
}
