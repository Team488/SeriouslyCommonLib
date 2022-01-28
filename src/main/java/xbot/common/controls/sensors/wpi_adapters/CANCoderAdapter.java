package xbot.common.controls.sensors.wpi_adapters;

import com.ctre.phoenix.sensors.CANCoder;
import com.ctre.phoenix.sensors.WPI_CANCoder;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XAbsoluteEncoder;
import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.injection.wpi_factories.DevicePolice.DeviceType;

public class CANCoderAdapter extends XAbsoluteEncoder {
    
    private CANCoder cancoder;

    @AssistedInject
    public CANCoderAdapter(@Assisted("deviceId") int deviceId, DevicePolice police) {
        this.cancoder = new WPI_CANCoder(deviceId);
        
        police.registerDevice(DeviceType.CAN, deviceId, this);
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
