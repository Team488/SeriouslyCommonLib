package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import xbot.common.controls.sensors.XGyro;

public record IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANBusId canBusId, int deviceId, MountOrientation mountOrientation,
        PowerSource powerFrom) {
    public IMUInfo(MountOrientation mountOrientation) {
        this("IMU", XGyro.ImuType.onboard, null, (CANBusId) null, 1, mountOrientation, PowerSource.RIO);
    }

    public IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANPort canPort, int deviceId) {
        this(name, imuType, interfaceType, CANBusId.fromPort(canPort), deviceId);
    }

    public IMUInfo(String name, XGyro.ImuType imuType, XGyro.InterfaceType interfaceType, CANPort canPort, int deviceId, MountOrientation mountOrientation,
            PowerSource powerFrom) {
        this(name, imuType, interfaceType, CANBusId.fromPort(canPort), deviceId, mountOrientation, powerFrom);
    }

    public IMUInfo(CANPort canPort, int deviceId) {
        this(CANBusId.fromPort(canPort), deviceId);
    }

    public IMUInfo(CANPort canPort, int deviceId, PowerSource powerFrom) {
        this(CANBusId.fromPort(canPort), deviceId, powerFrom);
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
        return new IMUInfo(realInstance.name(), XGyro.ImuType.mock, realInstance.interfaceType(), realInstance.canBusId(), realInstance.deviceId(), null,
                realInstance.powerFrom());
    }
}
