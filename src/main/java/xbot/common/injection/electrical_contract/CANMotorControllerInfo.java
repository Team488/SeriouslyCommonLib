package xbot.common.injection.electrical_contract;

public record CANMotorControllerInfo(
        String name,
        MotorControllerType type,
        CANBusId busId,
        int deviceId,
        CANMotorControllerOutputConfig outputConfig) {
}
