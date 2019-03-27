package edu.wpi.first.wpilibj;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.injection.DeviceInfo;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockDigitalInput extends XDigitalInput {

    protected boolean value;
    final int channel;

    @AssistedInject
    public MockDigitalInput(@Assisted("channel") int channel, DevicePolice police) {
        super(police, channel);
        this.channel = channel;
    }

    @AssistedInject
    public MockDigitalInput(@Assisted("deviceInfo") DeviceInfo deviceInfo, DevicePolice police) {
        super(police, deviceInfo.channel);
        this.channel = deviceInfo.channel;
        this.setInverted(deviceInfo.inverted);
    }

    public void setValue(boolean value) {
        this.value = value ^ getInverted();
    }
    
    @Override
    public void setInverted(boolean inverted) {
        super.setInverted(inverted);
        value = !value;
    }

    public boolean getRaw() {
        return value;
    }

    public int getChannel() {
        return this.channel;
    }
}
