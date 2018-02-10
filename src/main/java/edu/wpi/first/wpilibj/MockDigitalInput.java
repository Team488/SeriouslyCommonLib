package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import xbot.common.controls.sensors.XDigitalInput;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockDigitalInput extends XDigitalInput {

    protected boolean value;
    final int channel;

    @Inject
    public MockDigitalInput(@Assisted("channel") int channel, DevicePolice police) {
        super(police, channel);
        this.channel = channel;
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
