package xbot.common.injection.electrical_contract;

public record CANLightControllerInfo(
        String name,
        LightControllerType type,
        CANBusId canBusId,
        int deviceId,
        CANLightControllerOutputConfig outputConfig,
        PowerSource powerFrom
) {
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
