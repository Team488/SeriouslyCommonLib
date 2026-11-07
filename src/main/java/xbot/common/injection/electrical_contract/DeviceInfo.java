package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;

/**
 * Immutable device configuration. Construction does not validate or reserve hardware.
 *
 * <pre>{@code
 * DeviceInfo encoder = DeviceInfo.builder("SteeringEncoder")
 *         .withCanBus(CANPort.CAN_S1, 11)
 *         .withInverted(true)
 *         .build();
 * }</pre>
 *
 * @param name device name
 * @param channel channel or device number
 * @param inverted whether the device's reading is inverted
 * @param canBusId CAN bus identity, ignored by non-CAN devices
 * @param powerFrom power-source metadata
 */
public record DeviceInfo(String name, int channel, boolean inverted, CANBusId canBusId, PowerSource powerFrom) {
    /**
     * Starts a builder with no inversion, CAN_S0, and no power-source metadata.
     * Set a non-CAN channel with {@link Builder#withChannel(int)}, or a CAN address
     * with {@link Builder#withCanBus(CANPort, int)}. An unspecified channel is -1.
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private int channel = -1;
        private boolean inverted;
        private CANBusId canBusId = CANBusId.fromPort(CANPort.CAN_S0);
        private PowerSource powerFrom;

        private Builder(String name) {
            this.name = name;
        }

        public Builder withChannel(int channel) {
            this.channel = channel;
            return this;
        }

        public Builder withInverted(boolean inverted) {
            this.inverted = inverted;
            return this;
        }

        public Builder withCanBus(CANPort port, int deviceId) {
            return withCanBus(CANBusId.fromPort(port), deviceId);
        }

        public Builder withCanBus(CANBusId canBusId, int deviceId) {
            this.canBusId = canBusId;
            this.channel = deviceId;
            return this;
        }

        public Builder withPowerFrom(PowerSource powerFrom) {
            this.powerFrom = powerFrom;
            return this;
        }

        public DeviceInfo build() {
            return new DeviceInfo(name, channel, inverted, canBusId, powerFrom);
        }
    }
}