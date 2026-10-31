package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;
import org.wpilib.hardware.imu.OnboardIMU.MountOrientation;

import xbot.common.controls.sensors.XGyro;

/**
 * IMU configuration. No bus, interface, orientation, or power metadata is inferred from the IMU type.
 * Construction does not validate or reserve hardware.
 *
 * <pre>{@code
 * IMUInfo imu = IMUInfo.builder("IMU", XGyro.ImuType.onboard)
 *         .withDeviceId(1)
 *         .withMountOrientation(MountOrientation.FLAT)
 *         .withPowerFrom(PowerSource.RIO)
 *         .build();
 * }</pre>
 *
 * @param name device name
 * @param imuType IMU implementation type
 * @param interfaceType hardware interface
 * @param canBusId CAN bus identity
 * @param deviceId device number
 * @param mountOrientation onboard IMU mounting orientation
 * @param powerFrom power-source metadata
 */
public record IMUInfo(
        String name,
        XGyro.ImuType imuType,
        XGyro.InterfaceType interfaceType,
        CANBusId canBusId,
        int deviceId,
        MountOrientation mountOrientation,
        PowerSource powerFrom) {
    /**
     * Starts a builder with an explicit name and IMU type. All optional metadata defaults to null.
     * Set the CAN address with {@link Builder#withCanBus(CANPort, int)}, or a non-CAN device ID
     * with {@link Builder#withDeviceId(int)}. An unspecified device ID is -1.
     */
    public static Builder builder(String name, XGyro.ImuType imuType) {
        return new Builder(name, imuType);
    }

    /**
     * Preserves identity, interface, bus, and power metadata for a mock, without mounting orientation.
     */
    public static IMUInfo createMock(IMUInfo realInstance) {
        return builder(realInstance.name(), XGyro.ImuType.mock)
                .withInterfaceType(realInstance.interfaceType())
                .withCanBus(realInstance.canBusId(), realInstance.deviceId())
                .withPowerFrom(realInstance.powerFrom())
                .build();
    }

    public static final class Builder {
        private final String name;
        private final XGyro.ImuType imuType;
        private int deviceId = -1;
        private XGyro.InterfaceType interfaceType;
        private CANBusId canBusId;
        private MountOrientation mountOrientation;
        private PowerSource powerFrom;

        private Builder(String name, XGyro.ImuType imuType) {
            this.name = name;
            this.imuType = imuType;
        }

        public Builder withDeviceId(int deviceId) {
            this.deviceId = deviceId;
            return this;
        }

        public Builder withInterfaceType(XGyro.InterfaceType interfaceType) {
            this.interfaceType = interfaceType;
            return this;
        }

        public Builder withCanBus(CANPort port, int deviceId) {
            return withCanBus(CANBusId.fromPort(port), deviceId);
        }

        public Builder withCanBus(CANBusId canBusId, int deviceId) {
            this.canBusId = canBusId;
            this.deviceId = deviceId;
            return this;
        }

        public Builder withMountOrientation(MountOrientation mountOrientation) {
            this.mountOrientation = mountOrientation;
            return this;
        }

        public Builder withPowerFrom(PowerSource powerFrom) {
            this.powerFrom = powerFrom;
            return this;
        }

        public IMUInfo build() {
            return new IMUInfo(name, imuType, interfaceType, canBusId, deviceId, mountOrientation, powerFrom);
        }
    }
}
