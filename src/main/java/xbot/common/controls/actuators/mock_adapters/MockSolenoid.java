package xbot.common.controls.actuators.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import xbot.common.controls.actuators.XSolenoid;
import xbot.common.injection.DevicePolice;

public class MockSolenoid extends XSolenoid {
    final int channel;
    protected boolean on;

    @AssistedFactory
    public abstract static class MockSolenoidFactory implements XSolenoidFactory {
        public abstract MockSolenoid create(@Assisted("channel") int channel);
    }

    @AssistedInject
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
        return 15;
    }

}
