package xbot.common.controls.actuators.mock_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.Relay.Value;
import xbot.common.controls.actuators.XRelay;
import xbot.common.injection.DevicePolice;

public class MockRelay extends XRelay {

    private Value internalValue;
    
    @AssistedFactory
    public abstract static class MockRelayFactory implements XRelayFactory {
        public abstract MockRelay create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public MockRelay(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        
        internalValue = Value.kOff;
    }
    @Override
    public void set(Value value) {
       internalValue = value;
    }
    
    public Value get() {
        return internalValue;
    }

}
