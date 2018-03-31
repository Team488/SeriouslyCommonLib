package xbot.common.controls.actuators.mock_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Relay.Value;
import xbot.common.controls.actuators.XRelay;
import xbot.common.injection.wpi_factories.DevicePolice;

public class MockRelay extends XRelay {

    private Value internalValue;
    
    @Inject
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
