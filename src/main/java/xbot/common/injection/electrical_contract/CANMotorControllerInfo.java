package xbot.common.injection.electrical_contract;

import org.wpilib.hardware.bus.CANPort;

/**
 * Motor-controller configuration. Construction does not validate or reserve hardware.
 *
 * <pre>{@code
 * CANMotorControllerInfo motor = CANMotorControllerInfo.builder("Drive")
 *         .withType(MotorControllerType.TalonFx)
 *         .withCanBus(CANBusId.Canivore, 1)
 *         .withPdhPort(PDHPort.PDH01)
 *         .build();
 * }</pre>
 *
 * @param name device name
 * @param type motor-controller type
 * @param busId CAN bus identity
 * @param deviceId CAN device number
 * @param pdhPort power-distribution port metadata
 * @param outputConfig output configuration, retained by reference
 */
public record CANMotorControllerInfo(
        String name,
        MotorControllerType type,
        CANBusId busId,
        int deviceId,
        PDHPort pdhPort,
        CANMotorControllerOutputConfig outputConfig) {

    /**
     * Starts a SparkMax builder on CAN_S0 with no PDH port.
     * Set the CAN address with {@link Builder#withCanBus(CANPort, int)}; an unspecified device ID is -1.
     * Each build gets a fresh default output configuration unless one is explicitly supplied.
     */
    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static final class Builder {
        private final String name;
        private int deviceId = -1;
        private MotorControllerType type = MotorControllerType.SparkMax;
        private CANBusId busId = CANBusId.fromPort(CANPort.CAN_S0);
        private PDHPort pdhPort;
        private CANMotorControllerOutputConfig outputConfig;
        private boolean outputConfigSpecified;

        private Builder(String name) {
            this.name = name;
        }

        public Builder withType(MotorControllerType type) {
            this.type = type;
            return this;
        }

        public Builder withCanBus(CANPort port, int deviceId) {
            return withCanBus(CANBusId.fromPort(port), deviceId);
        }

        public Builder withCanBus(CANBusId busId, int deviceId) {
            this.busId = busId;
            this.deviceId = deviceId;
            return this;
        }

        public Builder withPdhPort(PDHPort pdhPort) {
            this.pdhPort = pdhPort;
            return this;
        }

        public Builder withOutputConfig(CANMotorControllerOutputConfig outputConfig) {
            this.outputConfig = outputConfig;
            this.outputConfigSpecified = true;
            return this;
        }

        public CANMotorControllerInfo build() {
            return new CANMotorControllerInfo(name, type, busId, deviceId, pdhPort,
                    outputConfigSpecified ? outputConfig : new CANMotorControllerOutputConfig());
        }
    }
}
