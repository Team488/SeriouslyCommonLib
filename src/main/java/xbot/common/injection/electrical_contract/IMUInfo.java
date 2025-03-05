package xbot.common.injection.electrical_contract;

import com.ctre.phoenix6.CANBus;
import xbot.common.controls.sensors.XGyro;

public record IMUInfo(XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId) {
    public IMUInfo(XGyro.InterfaceType interfaceType) {
        this(interfaceType, null, 0);
    }

    public IMUInfo(CANBusId canBusId, int deviceId) {
        this(null, canBusId, deviceId);
    }
}
