package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import xbot.common.controls.sensors.XDigitalInput;

public class MockDigitalInput extends XDigitalInput {

    protected boolean value;
    final int channel;

    public MockDigitalInput(int channel) {
        this.channel = channel;
    }

    public void set_value(boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public int getChannel() {
        return this.channel;
    }

    @Override
    public LiveWindowSendable getLiveWindowSendable() {
        return null;
    }

}
