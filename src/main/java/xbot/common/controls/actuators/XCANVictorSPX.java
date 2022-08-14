package xbot.common.controls.actuators;

import xbot.common.injection.DevicePolice;
import xbot.common.properties.PropertyFactory;

public abstract class XCANVictorSPX extends XCANTalon {

    public interface XCANVictorSPXFactory {
        XCANVictorSPX create(int deviceId);
    }

    public XCANVictorSPX(int deviceId, PropertyFactory propMan, DevicePolice police) {
        super(deviceId, propMan, police);
        this.deviceId = deviceId;
        this.propMan = propMan;
    }
}