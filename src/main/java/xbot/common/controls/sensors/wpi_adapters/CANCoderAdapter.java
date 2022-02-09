package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyFactory;

public class CANCoderAdapter extends XAbsoluteEncoder {
    
    private static final Logger log = Logger.getLogger(CANCoderAdapter.class);

    private final int deviceId;
    private final CANCoder cancoder;

    private final DoubleProperty magnetOffset;
    private final BooleanProperty inverted;

    @AssistedInject
    public CANCoderAdapter(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf) {
        pf.setPrefix(owningSystemPrefix);

        this.inverted = pf.createEphemeralProperty("Inverted", deviceInfo.inverted);
        this.magnetOffset = pf.createEphemeralProperty("Magnet offset", 0.0);
        
        this.cancoder = new WPI_CANCoder(deviceInfo.channel);
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
}
