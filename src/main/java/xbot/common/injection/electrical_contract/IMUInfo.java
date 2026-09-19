package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import xbot.common.controls.sensors.XGyro;

public record IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId, MountOrientation mountOrientation, PowerSource powerFrom) {
    public IMUInfo(MountOrientation mountOrientation) {
        this("IMU", XGyro.ImuType.onboard, null, null, 1, mountOrientation, PowerSource.RIO);
    }

    // Backward compatible constructors
    public IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId) {
        this(name, imuType, interfaceType, canBusId, deviceId, null, null);
    }

    public IMUInfo(CANBusId canBusId, int deviceId) {
        this("IMU", XGyro.ImuType.pigeon2, null, canBusId, deviceId, null, null);
    }

    public IMUInfo(CANBusId canBusId, int deviceId, PowerSource powerFrom) {
        this("IMU", XGyro.ImuType.pigeon2, null, canBusId, deviceId, null, powerFrom);
    }

    public static IMUInfo createMock(IMUInfo realInstance) {
        return new IMUInfo(realInstance.name(), XGyro.ImuType.mock, realInstance.interfaceType(), 
            realInstance.canBusId(), realInstance.deviceId(), null, realInstance.powerFrom());
    }
}
