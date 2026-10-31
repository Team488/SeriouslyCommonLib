package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;

/**
 * Light-controller configuration. Construction does not validate or reserve hardware.
 *
 * <pre>{@code
 * CANLightControllerInfo lights = CANLightControllerInfo.builder("Lights")
 *         .withCanBus(CANPort.CAN_S0, 60)
 *         .withPowerFrom(PowerSource.PDH01)
 *         .build();
 * }</pre>
 *
 * @param name device name
 * @param type light-controller type
 * @param canBusId CAN bus identity
 * @param deviceId CAN device number
 * @param outputConfig output configuration, retained by reference
 * @param powerFrom power-source metadata
 */
public record CANLightControllerInfo(
        String name,
        LightControllerType type,
        CANBusId canBusId,
        int deviceId,
        CANLightControllerOutputConfig outputConfig,
        PowerSource powerFrom
) {
    /**
     * Starts a CANdle builder on CAN_S0 with the existing default light configuration
     * and no power-source metadata. Set the CAN address with {@link Builder#withCanBus(CANPort, int)};
     * an unspecified device ID is -1.
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private int deviceId = -1;
        private LightControllerType type = LightControllerType.Candle;
        private CANBusId canBusId = CANBusId.fromPort(CANPort.CAN_S0);
        private CANLightControllerOutputConfig outputConfig = CANLightControllerOutputConfig.Default;
        private PowerSource powerFrom;

        private Builder(String name) {
            this.name = name;
        }

        public Builder withType(LightControllerType type) {
            this.type = type;
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

        public Builder withOutputConfig(CANLightControllerOutputConfig outputConfig) {
            this.outputConfig = outputConfig;
            return this;
        }

        public Builder withPowerFrom(PowerSource powerFrom) {
            this.powerFrom = powerFrom;
            return this;
        }

        public CANLightControllerInfo build() {
            return new CANLightControllerInfo(name, type, canBusId, deviceId, outputConfig, powerFrom);
        }
    }
}
