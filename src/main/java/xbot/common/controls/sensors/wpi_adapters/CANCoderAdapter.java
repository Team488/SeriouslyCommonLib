package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.injection.electrical_contract.DeviceInfo;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;
import xbot.common.properties.BooleanProperty;
import xbot.common.properties.PropertyFactory;

public class CANCoderAdapter extends XAbsoluteEncoder {
    
    private final int deviceId;
    private final CANCoder cancoder;
    private final BooleanProperty inverted;

    @AssistedInject
    public CANCoderAdapter(@Assisted("deviceInfo") DeviceInfo deviceInfo,
            @Assisted("owningSystemPrefix") String owningSystemPrefix,
            DevicePolice police, PropertyFactory pf) {
        pf.setPrefix(owningSystemPrefix);

        this.inverted = pf.createEphemeralProperty("Inverted", deviceInfo.inverted);
        
        this.cancoder = new WPI_CANCoder(deviceInfo.channel);
        this.cancoder.configSensorDirection(this.inverted.get());
        
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
}
