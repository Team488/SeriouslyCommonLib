package xbot.common.injection.electrical_contract;

import xbot.common.controls.sensors.XGyro;

public record IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId) {
    public IMUInfo(XGyro.InterfaceType interfaceType) {
        this("IMU", XGyro.ImuType.navX, interfaceType, null, 0);
    }

    public IMUInfo(CANBusId canBusId, int deviceId) {
        this("IMU", XGyro.ImuType.pigeon2, null, canBusId, deviceId);
    }

    public static IMUInfo createMock(IMUInfo realInstance) {
        return new IMUInfo(realInstance.name(), XGyro.ImuType.mock, realInstance.interfaceType(), realInstance.canBusId(), realInstance.deviceId());
    }
}
