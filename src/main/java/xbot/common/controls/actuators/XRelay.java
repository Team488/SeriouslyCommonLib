package xbot.common.controls.actuators;

import edu.wpi.first.wpilibj.Relay.Value;
import xbot.common.injection.DevicePolice;
import xbot.common.injection.DevicePolice.DeviceType;

public abstract class XRelay {
    
    protected int channel;
    protected boolean inverted;
    
    public interface XRelayFactory {
        XRelay create(int channel);
    }

    protected XRelay(int channel, DevicePolice police) {
        this.channel = channel;
        police.registerDevice(DeviceType.PWM, channel, this);
    }
    
    public void setForward() {
        Value valueToSet = Value.kForward;
        if (inverted) {
            valueToSet = Value.kReverse;
        }
        
        set(valueToSet);
    }
    
    public void setReverse() {
        Value valueToSet = Value.kReverse;
        if (inverted) {
            valueToSet = Value.kForward;
        }
        
        set(valueToSet);
    }
    
    public void stop() {
        set(Value.kOff);
    }
    
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }
    
    public abstract void set(Value value);
}
