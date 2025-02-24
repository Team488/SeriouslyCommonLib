package xbot.common.injection.electrical_contract;

import xbot.common.controls.sensors.XGyro;

public record IMUInfo(XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId) {
}
