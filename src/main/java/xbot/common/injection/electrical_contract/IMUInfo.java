package xbot.common.injection.electrical_contract;

import xbot.common.controls.sensors.XGyro;

public record IMUInfo(XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId) {
    public IMUInfo(XGyro.InterfaceType interfaceType) {
        this(interfaceType, CANBusId.RIO, 0);
    }
}
