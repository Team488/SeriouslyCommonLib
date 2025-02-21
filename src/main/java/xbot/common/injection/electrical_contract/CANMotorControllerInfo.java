package xbot.common.injection.electrical_contract;

public record CANMotorControllerInfo(
        String name,
        MotorControllerType type,
        CANBusId busId,
        int deviceId,
        CANMotorControllerOutputConfig outputConfig) {

    public CANMotorControllerInfo(String name, int deviceId) {
        this(name, MotorControllerType.SparkMax, CANBusId.RIO, deviceId);
    }

    public CANMotorControllerInfo(String name, MotorControllerType type, CANBusId busId, int deviceId) {
        this(name, type, busId, deviceId, new CANMotorControllerOutputConfig());
    }
}
