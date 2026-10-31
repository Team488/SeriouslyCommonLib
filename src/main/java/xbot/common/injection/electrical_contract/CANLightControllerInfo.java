package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;

public record CANLightControllerInfo(
        String name,
        LightControllerType type,
        CANBusId canBusId,
        int deviceId,
        CANLightControllerOutputConfig outputConfig,
        PowerSource powerFrom
) {
    public CANLightControllerInfo(String name, LightControllerType type, CANPort canPort, int deviceId) {
        this(name, type, CANBusId.fromPort(canPort), deviceId);
    }

    public CANLightControllerInfo(String name, LightControllerType type, CANPort canPort,
            int deviceId, CANLightControllerOutputConfig outputConfig) {
        this(name, type, CANBusId.fromPort(canPort), deviceId, outputConfig);
    }

    public CANLightControllerInfo(String name, LightControllerType type, CANPort canPort,
            int deviceId, CANLightControllerOutputConfig outputConfig, PowerSource powerFrom) {
        this(name, type, CANBusId.fromPort(canPort), deviceId, outputConfig, powerFrom);
    }

    public CANLightControllerInfo(
            String name,
            LightControllerType type,
            CANBusId canBusId,
            int deviceId,
            CANLightControllerOutputConfig outputConfig) {
        this(
                name,
                type,
                canBusId,
                deviceId,
                outputConfig,
                null
        );
    }

    public CANLightControllerInfo(
            String name,
            LightControllerType type,
            CANBusId canBusId,
            int deviceId) {
        this(
                name,
                type,
                canBusId,
                deviceId,
                CANLightControllerOutputConfig.Default,
                null
        );
    }
}
