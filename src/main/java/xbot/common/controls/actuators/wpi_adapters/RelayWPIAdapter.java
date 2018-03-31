package xbot.common.controls.actuators.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Value;
import xbot.common.controls.actuators.XRelay;
import xbot.common.injection.wpi_factories.DevicePolice;

public class RelayWPIAdapter extends XRelay {

    Relay internalRelay;
    
    @Inject
    public RelayWPIAdapter(@Assisted("channel") int channel, DevicePolice police) {
        super(channel, police);
        internalRelay = new Relay(channel);
    }
    
    @Override
    public void set(Value value) {
        internalRelay.set(value);
    }    
}
