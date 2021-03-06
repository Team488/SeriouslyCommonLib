package xbot.common.controls.actuators;

import xbot.common.injection.wpi_factories.DevicePolice;
import xbot.common.properties.PropertyFactory;

public abstract class XCANVictorSPX extends XCANTalon {

    public XCANVictorSPX(int deviceId, PropertyFactory propMan, DevicePolice police) {
        super(deviceId, propMan, police);
        this.deviceId = deviceId;
        this.propMan = propMan;
    }
}