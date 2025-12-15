package xbot.common.injection.electrical_contract;

import xbot.common.controls.sensors.XGyro;

public record IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId, PowerSource powerFrom) {
    // Backward compatible constructors
    public IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId) {
        this(name, imuType, interfaceType, canBusId, deviceId, null);
    }

    public IMUInfo(XGyro.InterfaceType interfaceType) {
        this("IMU", XGyro.ImuType.navX, interfaceType, null, 0, null);
    }

    public IMUInfo(XGyro.InterfaceType interfaceType, PowerSource powerFrom) {
        this("IMU", XGyro.ImuType.navX, interfaceType, null, 0, powerFrom);
    }

    public IMUInfo(CANBusId canBusId, int deviceId) {
        this("IMU", XGyro.ImuType.pigeon2, null, canBusId, deviceId, null);
    }

    public IMUInfo(CANBusId canBusId, int deviceId, PowerSource powerFrom) {
        this("IMU", XGyro.ImuType.pigeon2, null, canBusId, deviceId, powerFrom);
    }

    public static IMUInfo createMock(IMUInfo realInstance) {
        return new IMUInfo(realInstance.name(), XGyro.ImuType.mock, realInstance.interfaceType(), 
            realInstance.canBusId(), realInstance.deviceId(), realInstance.powerFrom());
    }
}
