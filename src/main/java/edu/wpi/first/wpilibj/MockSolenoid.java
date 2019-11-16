package edu.wpi.first.wpilibj;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockSolenoid extends XSolenoid {
    final int channel;
    protected boolean on;

    @Inject
    public MockSolenoid(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        this.channel = channel;
    }

    @Override
    public void set(boolean on) {
        this.on = on;
    }

    public boolean get() {
        return on;
    }

    @Override
    public int getMaxSupportedChannel() {
        return 7;
    }
}
