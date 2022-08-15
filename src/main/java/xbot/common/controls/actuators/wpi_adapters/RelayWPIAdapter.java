package xbot.common.controls.actuators.wpi_adapters;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import xbot.common.controls.actuators.XRelay;
import xbot.common.injection.DevicePolice;

public class RelayWPIAdapter extends XRelay {

    Relay internalRelay;
    
    @AssistedFactory
    public abstract static class RelayWPIAdapterFactory implements XRelayFactory {
        public abstract RelayWPIAdapter create(@Assisted("channel") int channel);
    }

    @AssistedInject
    public RelayWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        internalRelay = new Relay(channel);
    }
    
    @Override
    public void set(Value value) {
        internalRelay.set(value);
    }    
}
